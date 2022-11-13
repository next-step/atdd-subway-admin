package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.Isolationer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static nextstep.subway.station.acceptance.StationAcceptaneRequest.지하철역을_생성한다;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * REST Assured 특징
 * given-when-them 패턴을 이용한 코드를 작성합니다.
 *
 * given : Test setup (테스트시 필요한 데이터, 및 파라미터를 셋팅합니다.)
 * when : Test action (Method type을 정의해줍니다.)
 * then : Test verification (Response Data를 검증합니다.)
 *
 * GET type인 경우에는, query parameters로
 * POST type인 경우에는 form parameters로 인식합니다.
 * PUT, POST type에서 query parameter와 form parameter를 함께 사용할 때는 정확하게 명시해주어야 합니다.
 */

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {

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

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        String deletableStationName = "강남역";
        ExtractableResponse<Response> response = 지하철역을_생성한다(deletableStationName);

        // then
        지하철역이_생성된다(response);

        // then

        List<String> stationNames = 모든_지하철역을_조회한다().jsonPath().getList("name", String.class);
        조회목록에서_해당_지하철을_찾을_수_있다(stationNames, deletableStationName);

    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        String deletableStationName = "강남역";
        지하철역을_생성한다(deletableStationName);

        // when
        ExtractableResponse<Response> response = 지하철역을_생성한다(deletableStationName);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // Given
        지하철역을_2개_생성한다();

        //When
        ExtractableResponse<Response> result = 모든_지하철역을_조회한다();

        //Then
        두개의_지하철역을_응답_받는다(result);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        //Given
        String deletableStationName = "강남역";
        int stationId = 지하철역을_생성한다(deletableStationName).jsonPath().getInt("id");

        //When
        해당_지하철역을_제거한다(stationId);

        //Then
        해당_지하철을_찾을_수_없다(stationId);

    }


    public ExtractableResponse<Response> 해당_지하철역을_제거한다(int stationId) {
        return RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete("/stations/" + stationId)
            .then().log().all()
            .extract();
    }

    public static void 지하철역을_2개_생성한다() {
        지하철역을_생성한다("강남역");
        지하철역을_생성한다("역삼역");
    }


    public static ExtractableResponse<Response> 모든_지하철역을_조회한다() {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/stations")
            .then().log().all()
            .extract();
    }

    public static void 두개의_지하철역을_응답_받는다(ExtractableResponse<Response> result) {
        assertThat(result.jsonPath().getList("name", String.class)).hasSize(2);
    }

    private static ExtractableResponse<Response> 특정_지하철역을_조회한다(int stationId) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/stations/" + stationId)
            .then().log().all()
            .extract();
    }

    public static void 해당_지하철을_찾을_수_없다(int stationId) {
        assertThat(특정_지하철역을_조회한다(stationId).statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());

    }

    public static void 지하철역이_생성된다(ExtractableResponse<Response> result) {
        assertThat(result.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    }

    public static void 조회목록에서_해당_지하철을_찾을_수_있다(List<String> actual, String expect) {
        assertThat(actual).containsAnyOf(expect);
    }

}
