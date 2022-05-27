package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * WHEN 지하철 노선을 생성하면
     * THEN 지하철 노선이 생성되고
     * THEN 지하철 노선을 조회 시 생성한 노선을 볼 수 있다
     */
    @DisplayName("지하철노선 생성")
    @Test
    void 지하철노선_생성_조회_성공() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/lines")
                .then().log().all()
                .extract();

        //then
        String lineName = response.jsonPath().get("name");
        assertThat(lineName).isEqualTo("2호선");
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED);
    }
}
