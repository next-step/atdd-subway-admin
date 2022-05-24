package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @Test
    void 지하철역을_생성한다() {
        // when
        ExtractableResponse<Response> response = 지하철역_생성("강남역").extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = 리스트로_추출(지하철역_조회(), String.class, "name");
        assertThat(stationNames).containsAnyOf("강남역");
    }

    private ValidatableResponse 지하철역_생성(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        return RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/stations")
                        .then().log().all();
    }

    private ValidatableResponse 지하철역_조회() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all();
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @Test
    void 기존에_존재하는_지하철역_이름으로_지하철역을_생성한다() {
        // given
        지하철역_생성("강남역");

        // when
        ExtractableResponse<Response> response = 지하철역_생성("강남역").extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @Test
    void 지하철역을_조회한다() {
        // given
        지하철역_생성("강남역");
        지하철역_생성("잠실역");

        // when
        List<StationResponse> stationResponses = 리스트로_추출(지하철역_조회(), StationResponse.class, ".");

        // then
        assertThat(stationResponses).hasSize(2);
    }

    private <T> List<T> 리스트로_추출(ValidatableResponse validatableResponse, Class<T> classType, String path) {
        return validatableResponse.extract()
                .jsonPath()
                .getList(path, classType);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @Test
    void 지하철역을_제거한다() {
    }
}
