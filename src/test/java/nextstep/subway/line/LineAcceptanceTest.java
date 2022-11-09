package nextstep.subway.line;

import io.restassured.RestAssured;
import nextstep.subway.Isolationer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;

public class LineAcceptanceTest {

    @LocalServerPort
    int port;
    @Autowired
    private Isolationer dbIsolation;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        dbIsolation.excute();
    }




}
