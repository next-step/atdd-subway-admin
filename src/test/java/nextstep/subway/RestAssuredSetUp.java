package nextstep.subway;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.web.server.LocalServerPort;

public class RestAssuredSetUp {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

}
