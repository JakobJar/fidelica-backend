package org.fidelica.backend.rest.user;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.fidelica.backend.repository.user.UserRepository;
import org.fidelica.backend.user.StandardUser;

import java.util.regex.Pattern;

public class UserRegistrationController {

    private final UserRepository userRepository;

    private final Pattern usernamePattern;
    private final Pattern emailPattern;
    private final Pattern passwordPattern;

    public UserRegistrationController(@NonNull UserRepository userRepository) {
        this.userRepository = userRepository;

        this.usernamePattern = Pattern.compile("^[\\w.]{3,16}$");
        this.emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$");
        this.passwordPattern = Pattern.compile("^(?=.*[A-Z])(?=.*[!@#$&*%])(?=.*\\d)(?=.*[a-z]).{8,32}$");
    }

    public void createUser(Context context) {
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

        // TODO: recaptcha
        userRepository.create(new StandardUser(ObjectId.get(), username, email, password));
        // TODO: Set session
        context.result("Success");
    }
}
