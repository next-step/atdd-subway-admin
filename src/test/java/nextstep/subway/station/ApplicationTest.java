package nextstep.subway.station;

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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("지하철 역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest {
    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    /**
     * 테스트 환경 구축 (Spring Boot Test)
     * 테스트 클라이언트 설정 (RestAssured)
     * 지하철역 생성 인수 테스트
     * - When 지하철역을 생성한다.
     * - Then 지하철역이 생성된다.
     */

    @Test
    @DisplayName("지하철역 생성")
    void create() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");

        ExtractableResponse<Response> response = 지하철역_생성(params);

        String name = response.jsonPath().getString("name");
        assertThat(name).isEqualTo("강남역");
    }

    /**
     * Given: 지하철 역 두개를 생성한다.
     * When: 지하철역 목록을 조회한다.
     * Then: 2개의 지하철역을 응답빧는다.
     */
    @Test
    @DisplayName("지하철역 조회")
    void showList() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "강남역");
        지하철역_생성(params1);

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "역삼역");
        지하철역_생성(params2);

        // when
        ExtractableResponse<Response> response = 지하철역_조회();
        assertThat(response.jsonPath().getList(".").size()).isEqualTo(2);
    }

    private ExtractableResponse<Response> 지하철역_조회() {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/stations")
                .then().log().all().extract();

        // then
        지하철역_조회_검증(response);
        return response;
    }

    private void 지하철역_조회_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> 지하철역_생성(Map<String, String> params) {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();

        지하철역이_생성됨_검증(response);

        return response;
    }

    private void 지하철역이_생성됨_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }
}
