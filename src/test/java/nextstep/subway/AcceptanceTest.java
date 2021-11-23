package nextstep.subway;

import static org.assertj.core.api.Assertions.*;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }

        databaseCleanup.execute();
    }

    protected static String 로케이션_가져오기(final ExtractableResponse<Response> response) {
        return response.header("Location");
    }

    protected static long 아이디_추출하기(final String location) {
        return Long.parseLong(location.split("/")[2]);
    }

    protected static long 아이디_추출하기(final ExtractableResponse<Response> response) {
        return Long.parseLong(로케이션_가져오기(response).split("/")[2]);
    }

    protected static void 응답코드_검증(final ExtractableResponse<Response> response, final int statusCode) {
        assertThat(response.statusCode()).isEqualTo(statusCode);
    }
}
