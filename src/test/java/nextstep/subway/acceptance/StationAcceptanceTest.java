package nextstep.subway.acceptance;

import static nextstep.subway.test.RequestUtils.*;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StationAcceptanceTest {

    private static final Map<String, Object> 강남역 = new HashMap<>();
    private static final Map<String, Object> 서울역 = new HashMap<>();
    private static final Map<String, Object> 신림역 = new HashMap<>();

    public static final String STATION_PATH = "/stations";
    static {
        강남역.put("name", "강남역");
        서울역.put("name", "서울역");
        신림역.put("name", "신림역");
    }

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * When 지하철역을 2개를 생성하면
     * Then 지하철역 2개가 생성된다
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     * When 지하철역 목록 조회시
     * Then 2개의 지하철역을 응답 받는다
     * When 2개 중 1개 지하철역을 삭제하면
     * Then 그 지하철역은 목록 조회 시 생성한 역을 찾을 수 없다
     * */
    @DisplayName("지하철역을 관리한다.")
    @Test
    void manageStations() {
        // when
        List<ExtractableResponse<Response>> createResponses = requestCreateBundle(Arrays.asList(강남역,서울역),STATION_PATH);

        // then
        지하철역들이_생성되었는지_검증(createResponses);

        // when
        ExtractableResponse<Response> createResponse = requestCreate(강남역, STATION_PATH);

        // then
        존재하는_지하철역인_경우_오류_응답_인지_검증(createResponse);

        // when
        ExtractableResponse<Response> getAllResponse = requestGetAll(STATION_PATH);

        //then
        지하철역_목록_조회_검증(getAllResponse);

        //when

        ExtractableResponse<Response> deleteResponse = requestDeleteById(STATION_PATH,extractId(requestCreate(신림역,STATION_PATH)));
        HTTP_응답_상태코드_검증(deleteResponse, HttpStatus.NO_CONTENT);

        //then
        ExtractableResponse<Response> response = requestGetAll(STATION_PATH);
        지하철역이_삭제_되었는지_검증(response);
    }


    private void 존재하는_지하철역인_경우_오류_응답_인지_검증(ExtractableResponse<Response> createResponse) {
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철역들이_생성되었는지_검증(List<ExtractableResponse<Response>> createResponses) {
        for (ExtractableResponse<Response> createResponse : createResponses) {
            assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        }
    }

    private void HTTP_응답_상태코드_검증(ExtractableResponse<Response> response, HttpStatus httpStatus) {
        assertThat(response.statusCode()).isEqualTo(httpStatus.value());
    }

    private void 지하철역_목록_조회_검증(ExtractableResponse<Response> getAllResponse) {
        assertAll(
                () -> assertThat(getAllResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(getAllResponse.jsonPath().getList("name")).contains("강남역", "서울역")
        );
    }

    private void 지하철역이_삭제_되었는지_검증(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("name")).doesNotContain("신림역"),
                () -> assertThat(response.jsonPath().getList("name")).contains("강남역","서울역")
        );
    }
    private long extractId(ExtractableResponse<Response> createResponse) {
        return createResponse.body().jsonPath().getLong("id");
    }
}
