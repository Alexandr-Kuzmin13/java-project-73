package hexlet.code;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootApplication
public class AppTest {

    @Test
    void contextLoads() {
        assertThat(true).isTrue();
    }
}
