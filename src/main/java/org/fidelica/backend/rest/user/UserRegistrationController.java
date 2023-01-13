package org.fidelica.backend.rest.user;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.InternalServerErrorResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.fidelica.backend.repository.user.UserRepository;
import org.fidelica.backend.user.StandardUser;
import org.fidelica.backend.user.login.PasswordHandler;
import org.fidelica.backend.user.login.PasswordHash;

import java.security.spec.InvalidKeySpecException;
import java.util.regex.Pattern;

@Slf4j
public class UserRegistrationController {

    private final UserRepository userRepository;
    private final PasswordHandler passwordHandler;

    private final Pattern usernamePattern;
    private final Pattern emailPattern;
    private final Pattern passwordPattern;

    public UserRegistrationController(@NonNull UserRepository userRepository, @NonNull PasswordHandler passwordHandler) {
        this.userRepository = userRepository;
        this.passwordHandler = passwordHandler;

        this.usernamePattern = Pattern.compile("^[\\w.]{3,16}$");
        this.emailPattern = Pattern.compile("^[\\w-.]+@[\\w-]+\\.+[\\w-]{2,}$");
        this.passwordPattern = Pattern.compile("^(?=.*[A-Z])(?=.*[!@#$&*%])(?=.*\\d)(?=.*[a-z]).{8,32}$");
    }

    public void createUser(@NonNull Context context) {
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
        // TODO: recaptcha
        userRepository.create(new StandardUser(ObjectId.get(), username, email, passwordHash));
        // TODO: Set session
        context.result("Success");
    }
}
