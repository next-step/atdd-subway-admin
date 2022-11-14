package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import nextstep.subway.BaseAcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends BaseAcceptanceTest {

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> 지하철역_생성_응답 = 지하철역_생성_요청("강남역");

        // then
        assertThat(지하철역_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        assertThat(지하철역_이름_목록_조회_요청()).containsAnyOf("강남역");
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
        지하철역_생성_요청("강남역");

        // when
        ExtractableResponse<Response> 지하철역_생성_응답 = 지하철역_생성_요청("강남역");

        // then
        assertThat(지하철역_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        지하철역_생성_요청("강남역");
        지하철역_생성_요청("양재역");

        // when
        List<String> 지하철역_이름_목록_조회_응답 = 지하철역_이름_목록_조회_요청();

        // then
        assertThat(지하철역_이름_목록_조회_응답).containsAnyOf("강남역", "양재역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> 지하철역_생성_응답 = 지하철역_생성_요청("강남역");

        // when
        지하철역_삭제_요청(객체_응답_ID(지하철역_생성_응답));

        // then
        assertThat(지하철역_조회_요청("강남역")).isEmpty();
    }

    public static ExtractableResponse<Response> 지하철역_생성_요청(String name){
        return RestAssured.given().log().all()
                .body(createParam(name))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    private static List<String> 지하철역_조회_요청(String name) {
        return RestAssured.given().log().all()
                .body(createParam(name))
                .when().get("/stations")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }

    private static List<String> 지하철역_이름_목록_조회_요청() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }

    private void 지하철역_삭제_요청(long id) {
        RestAssured.given().log().all()
                .when().delete("/stations/" + id)
                .then().log().all();
    }

    private static HashMap<Object, Object> createParam(String name) {
        HashMap<Object, Object> params = new HashMap<>();
        params.put("name", name);
        return params;
    }

}
