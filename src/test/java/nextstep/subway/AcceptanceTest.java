package nextstep.subway;

import io.restassured.RestAssured;
import nextstep.subway.db.DataInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    public void databaseClean(String... tables) {
        dataInitializer.execute(tables);
    }
}
