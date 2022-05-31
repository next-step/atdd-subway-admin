package nextstep.subway;

import io.restassured.RestAssured;
import nextstep.subway.utils.RequestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

@Sql("classpath:truncate.sql")
@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BasicAcceptance {
    @LocalServerPort
    private int port;

    public static RequestUtil requestUtil = new RequestUtil();

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        beforeEachInit();
    }

    protected abstract void beforeEachInit();

}
