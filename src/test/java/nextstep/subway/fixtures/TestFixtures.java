package nextstep.subway.fixtures;

import io.restassured.RestAssured;
import nextstep.subway.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class TestFixtures {

    @LocalServerPort
    int port;
    @Autowired
    DatabaseCleanup databaseCleanup;

    @BeforeEach
    protected void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();
    }
}
