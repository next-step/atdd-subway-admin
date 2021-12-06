package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.util.UrlConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)  --> 메소드 실행 전 새로운 context를 실행. 단, 실행시간에 대한 단점 존재
public class StationAcceptanceTest extends AcceptanceTest {



    private Map<String, String> params = new HashMap<>();

    @BeforeEach
    void makeDefaultStationParam() {
        params = new HashMap<>();
        params.put("name", "강남역");
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        // @BeforeEach로 대체

        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청(params);

        // then
        응답_코드_검증(response, HttpStatus.CREATED.value());
        응답_헤더_정보_존재여부_검증(response);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철역_생성_요청(params);

        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청(params);

        // then
        응답_코드_검증(response, HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = 지하철역_생성_요청(params);

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "역삼역");
        ExtractableResponse<Response> createResponse2 = 지하철역_생성_요청(params2);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get(UrlConstants.STATION_DEFAULT_URL)
                .then().log().all()
                .extract();

        // then
        응답_코드_검증(response, HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        지하철_노선_목록_포함여부_검증(resultLineIds, expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = 지하철역_생성_요청(params);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        응답_코드_검증(response, HttpStatus.NO_CONTENT.value());
    }

    /**
     * 지하철역 생성요청
     * @param params
     * @return
     */
    private ExtractableResponse<Response> 지하철역_생성_요청(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(UrlConstants.STATION_DEFAULT_URL)
                .then().log().all()
                .extract(); // extract가 객체로 반환해줌.
    }

    // 검증 코드는 Line과 동일하므로 Class 분리하여 관리하기.
    private void 응답_코드_검증(ExtractableResponse<Response> response, int statusCode) {
        assertThat(response.statusCode()).isEqualTo(statusCode);
    }

    private void 응답_헤더_정보_존재여부_검증(ExtractableResponse<Response> response) {
        assertThat(response.header("Location")).isNotBlank();
    }

    public void 지하철_노선_목록_포함여부_검증(List<Long> resultLineIds, List<Long> expectedLineIds) {
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }
}
