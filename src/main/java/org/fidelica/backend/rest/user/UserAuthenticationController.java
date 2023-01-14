package org.fidelica.backend.rest.user;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.UnauthorizedResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.fidelica.backend.repository.user.UserRepository;
import org.fidelica.backend.user.StandardUser;
import org.fidelica.backend.user.login.PasswordHandler;
import org.fidelica.backend.user.login.PasswordHash;
import org.fidelica.backend.util.GoogleRecaptcha;

import java.io.IOException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

@Slf4j
public class UserAuthenticationController {

    private final UserRepository userRepository;
    private final PasswordHandler passwordHandler;
    private final GoogleRecaptcha recaptcha;

    private final Pattern usernamePattern;
    private final Pattern emailPattern;
    private final Pattern passwordPattern;

    private final Lock registrationLock;

    public UserAuthenticationController(@NonNull UserRepository userRepository, @NonNull PasswordHandler passwordHandler, @NonNull GoogleRecaptcha recaptcha){
        this.userRepository = userRepository;
        this.passwordHandler = passwordHandler;
        this.recaptcha = recaptcha;

        this.usernamePattern = Pattern.compile("^[\\w.]{3,16}$");
        this.emailPattern = Pattern.compile("^[\\w-.]+@[\\w-]+\\.+[\\w-]{2,}$");
        this.passwordPattern = Pattern.compile("^(?=.*[A-Z])(?=.*[!@#$&*%])(?=.*\\d)(?=.*[a-z]).{8,32}$");

        this.registrationLock = new ReentrantLock();
    }

    public void register(@NonNull Context context) {
        var recaptchaResponse = context.formParam("g-recaptcha");
        var username = context.formParam("username");
        var email = context.formParam("email");
        var password = context.formParam("password");
        if (username == null || email == null || password == null)
            throw new BadRequestResponse("Incomplete form data");

        if (!usernamePattern.matcher(username).matches())
            throw new BadRequestResponse("Invalid username");
        if (!emailPattern.matcher(email).matches())
            throw new BadRequestResponse("Invalid email");
        if (!passwordPattern.matcher(password).matches())
            throw new BadRequestResponse("Invalid password");

        try {
            if (!recaptcha.isValid(recaptchaResponse))
                throw new UnauthorizedResponse("Invalid recaptcha");
        } catch (IOException | InterruptedException e) {
            log.error("Couldn't verify recaptcha", e);
            throw new UnauthorizedResponse("Couldn't verify recaptcha");
        }

        registrationLock.lock();
        try {
            if (userRepository.isUserNameExisting(username))
                throw new BadRequestResponse("Username already in use");
            if (userRepository.isEmailExisting(email))
                throw new BadRequestResponse("Email already in use");

            PasswordHash passwordHash;
            try {
                passwordHash = passwordHandler.generateHash(password);
            } catch (InvalidKeySpecException e) {
                log.error("Error while hashing password", e);
                throw new InternalServerErrorResponse("Error while hashing password");
            }
            var user = new StandardUser(ObjectId.get(), username, email, passwordHash);
            userRepository.create(user);
            context.sessionAttribute("user", user);
            context.json(user);
        } finally {
            registrationLock.unlock();
        }
    }

    public void login(@NonNull Context context) {
        var recaptchaResponse = context.formParam("g-recaptcha");
        var username = context.formParam("username");
        var password = context.formParam("password");

        if (username == null || password == null || recaptchaResponse == null)
            throw new BadRequestResponse("Incomplete form data");

        try {
            if (!recaptcha.isValid(recaptchaResponse))
                throw new UnauthorizedResponse("Invalid recaptcha");
        } catch (IOException | InterruptedException e) {
            log.error("Couldn't verify recaptcha", e);
            throw new UnauthorizedResponse("Couldn't verify recaptcha");
        }

        userRepository.findByUserNameOrEmail(username).ifPresentOrElse(user -> {
            try {
                if(!passwordHandler.validatePassword(user.getPasswordHash(), password))
                    throw new UnauthorizedResponse("username or password doesn't match");
            } catch (InvalidKeySpecException e) {
                log.error("Error while hashing password", e);
                throw new InternalServerErrorResponse("Error while hashing password");
            }

            context.sessionAttribute("user", user);
            context.json(user);
        }, () -> {
            throw new UnauthorizedResponse("Invalid username/email or password");
        });
    }

    public void logout(@NonNull Context context) {
        context.req().getSession().invalidate();
        context.json("Success");
    }
}
