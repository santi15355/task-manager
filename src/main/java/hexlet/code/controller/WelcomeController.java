package hexlet.code.controller;


import com.rollbar.notifier.Rollbar;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class WelcomeController {

    @Autowired
    private final Rollbar rollbar;

    public String root() {
        rollbar.debug("Welcome to Spring -> Rollbar");
        return "Welcome to Spring";
    }

}
