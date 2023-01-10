package hexlet.code;

import com.rollbar.notifier.Rollbar;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.rollbar.spring.webmvc.RollbarSpringConfigBuilder.withAccessToken;

@SpringBootApplication
@ComponentScan(basePackages = "hexlet.code")
@SecurityScheme(name = "javainuseapi", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class AppApplication extends SpringBootServletInitializer {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(AppApplication.class, args);
        rollbarStart();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static void rollbarStart() throws Exception {
        Rollbar rollbar = Rollbar.init(withAccessToken("8425592a0bc94e2ab85389df2f793ad7")
                .environment("qa")
                .codeVersion("1.0.0")
                .build());

        rollbar.log("Hello, Rollbar");
        rollbar.close(true);
    }

}

