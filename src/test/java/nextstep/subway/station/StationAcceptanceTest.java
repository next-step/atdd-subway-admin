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
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    private ExtractableResponse<Response> 지하철_역_생성(final String 지하철역_이름){
        Map<String, String> params = new HashMap<>();
        params.put("name", 지하철역_이름);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }
    private ExtractableResponse<Response> 지하철역_목록_조회(){
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철역_삭제_요청(int id) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .when().delete("/stations/{id}")
                .then().log().all()
                .extract();
    }

    private void 지하철역_목록_존재한다(ExtractableResponse<Response> 지하철역_목록, String...지하철들) {
        assertAll(
                ()->assertThat(지하철역_목록.statusCode()).isEqualTo(HttpStatus.OK.value()),
                ()->assertThat(지하철역_목록.jsonPath().getList("name", String.class)).containsExactly(지하철들)
        );
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
        ExtractableResponse<Response> 지하철역_생성_결과 = 지하철_역_생성("강남역");

        // then
        assertThat(지하철역_생성_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        ExtractableResponse<Response> 지하철역_목록 = 지하철역_목록_조회();
        지하철역_목록_존재한다(지하철역_목록, "강남역");
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
        String 강남역 = 지하철_역_생성("강남역").body().jsonPath().get("name");

        // when
        ExtractableResponse<Response> response = 지하철_역_생성(강남역);

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
        // given
        String 강남역 = 지하철_역_생성("강남역").body().jsonPath().get("name");
        String 역삼역 = 지하철_역_생성("역삼역").body().jsonPath().get("name");

        // when
        ExtractableResponse<Response> 지하철역_목록 = 지하철역_목록_조회();

        // then
        지하철역_목록_존재한다(지하철역_목록, 강남역, 역삼역);

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
        int 강남역_아이디 = 지하철_역_생성("강남역").body().jsonPath().get("id");
        int 역삼역_아이디 = 지하철_역_생성("역삼역").body().jsonPath().get("id");

        // when
        ExtractableResponse<Response> 지하철역_삭제_결과 = 지하철역_삭제_요청(강남역_아이디);

        // then
        List<Integer> 저장된_아이디_리스트 = 지하철역_목록_조회().jsonPath().getList("id");
        assertAll(
                ()->assertThat(지하철역_삭제_결과.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                ()->assertThat(저장된_아이디_리스트).containsExactly(역삼역_아이디)
        );
    }
}
