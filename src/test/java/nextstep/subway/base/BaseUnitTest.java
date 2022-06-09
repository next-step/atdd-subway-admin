package nextstep.subway.base;

import io.restassured.RestAssured;
import nextstep.subway.Db.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseUnitTest {
    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleanup databaseCleanup;

    private final String LINE_URL = "/lines";

    @BeforeEach
    public void setUp() throws Exception {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }
        databaseCleanup.tableClear();
    }
}
