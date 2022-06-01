package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given
        final String lineName = "신분당선";

        // when
        final ExtractableResponse<Response> createResponse = 지하철_노선을_생성한다(lineName);

        // then
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        final List<String> lineNames = 지하철_노선_목록을_조회한다();
        assertThat(lineNames).contains(lineName);
    }

    private ExtractableResponse<Response> 지하철_노선을_생성한다(final String name) {
        final Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", "bg-red-600");
        params.put("upStationId", 1L);
        params.put("downStationId", 2L);
        params.put("distance", 10);

        // when
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private List<String> 지하철_노선_목록을_조회한다() {
        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract()
                .jsonPath().getList("name", String.class);
    }
}
