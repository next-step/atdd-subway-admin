package nextstep.subway.base;

import io.restassured.RestAssured;
import nextstep.subway.helper.TestIsolator;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;

public class AcceptanceTest {
    @LocalServerPort
    int port;
    @Autowired
    private TestIsolator testIsolator;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        testIsolator.run();
    }
}
