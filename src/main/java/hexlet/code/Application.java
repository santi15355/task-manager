package hexlet.code;

import com.rollbar.notifier.Rollbar;
import com.rollbar.notifier.config.Config;

import static com.rollbar.spring.webmvc.RollbarSpringConfigBuilder.withAccessToken;

public class Application implements AutoCloseable {
    private Rollbar rollbar;

    public Application() {
        Config config = withAccessToken("8425592a0bc94e2ab85389df2f793ad7")
                .environment("production")
                .codeVersion("1.0.0")
                .build();
        this.rollbar = Rollbar.init(config);
    }

    @Override
    public void close() throws Exception {
        this.rollbar.close(true);
    }

    public static void main(String[] args) throws Exception {
        try (Application app = new Application()) {
            app.execute();
        }
    }

    private void execute() {
        try {
            throw new RuntimeException("Some error");
        } catch (Exception e) {
            rollbar.error(e, "Hello, Rollbar");
        }
    }
}
