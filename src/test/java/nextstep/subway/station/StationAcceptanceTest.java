package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public void createStation() {
        //given
        String 생성_요청_역_이름 = "강남역";

        // when
        ExtractableResponse<Response> response = 역_생성_요청(생성_요청_역_이름);

        // then
        생성됨_확인(response);

        // then
        ExtractableResponse<Response> 역_조회_응답 = 역_조회_요청();
        역_포함_확인(역_조회_응답, 생성_요청_역_이름);
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    public void createStationWithDuplicateName() {
        // given
        String 강남역 = "강남역";
        역_생성_요청(강남역);

        // when
        ExtractableResponse<Response> 역_생성_응답 = 역_생성_요청(강남역);

        // then
        역_생성_실패(역_생성_응답);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    public void getStations() {
        //given
        String 강남역 = "강남역";
        String 교대역 = "교대역";
        역_생성_요청(강남역);
        역_생성_요청(교대역);

        //when
        ExtractableResponse<Response> 역_조회_응답 = 역_조회_요청();

        //then
        역_포함_확인(역_조회_응답, 강남역, 교대역);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    public void deleteStation() {
        //given
        String 강남역 = "강남역";
        Long 생성_역_id = 역_생성_요청(강남역).jsonPath().getLong("id");

        //when
        역_제거_요청(생성_역_id);

        //then
        ExtractableResponse<Response> 역_조회_응답 = 역_조회_요청();
        이름_불포함_확인(역_조회_응답, 강남역);
    }

    private ExtractableResponse<Response> 역_생성_요청(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/stations")
            .then().log().all()
            .extract();
    }

    private void 역_포함_확인(ExtractableResponse<Response> response, String... stationNames) {
        List<String> responseNames = response.jsonPath().getList("name", String.class);
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.statusCode()),
            () -> assertThat(responseNames).containsAll(Arrays.asList(stationNames))
        );

    }

    private ExtractableResponse<Response> 역_조회_요청() {
        return RestAssured.given().log().all()
            .when().get("/stations")
            .then().log().all()
            .extract();
    }

    private void 역_생성_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 역_제거_요청(Long id) {
        RestAssured.given().log().all()
            .when().delete("/stations/{id}", id)
            .then().log().all()
            .extract();
    }

}
