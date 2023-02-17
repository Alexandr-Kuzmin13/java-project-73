package hexlet.code;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class WelcomeController {

    @GetMapping("/welcome")

    public String welcomeUser() {
        return "Welcome to Spring";
    }
}
