package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StationAcceptanceTest {
    @LocalServerPort
    int port;

    private Map<String, String> 강남역, 선릉역;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        강남역 = StationParameterMaker.생성("강남역");
        선릉역 = StationParameterMaker.생성("선릉역");
    }

    /**
     * When 지하철역을 생성하면 Then 지하철역이 생성된다 Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> 생성된_강남역 = 지하철역_생성(강남역);

        // then
        assertThat(생성된_강남역.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        assertThat(지하철역_목록()).containsAnyOf("강남역");
    }

    /**
     * Given 지하철역을 생성하고 When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면 Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철역_생성(강남역);

        // when
        ExtractableResponse<Response> 생성_실패된_강남역 = 지하철역_생성(강남역);

        // then
        assertThat(생성_실패된_강남역.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철역을 생성하고 When 지하철역 목록을 조회하면 Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        Arrays.asList(선릉역, 강남역).forEach(지하철역 -> 지하철역_생성(지하철역));

        // when
        final List<String> 역목록 = 지하철역_목록();

        // then
        assertAll(
                () -> assertThat(역목록).hasSize(2),
                () -> assertThat(역목록).contains("강남역", "선릉역")
        );
    }

    /**
     * Given 지하철역을 생성하고 When 그 지하철역을 삭제하면 Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> 생성된_강남역 = 지하철역_생성(강남역);

        // when
        지하철역_삭제(생성된_강남역);

        // then
        assertThat(지하철역_목록()).doesNotContain("강남역");
    }

    private void 지하철역_삭제(ExtractableResponse<Response> response) {
        RestAssured.given().log().all()
                .when().delete("/stations/" + response.body().jsonPath().get("id"))
                .then().log().all();
    }

    private ExtractableResponse<Response> 지하철역_생성(Map<String, String> 역이름) {
        return RestAssured.given().log().all()
                .body(역이름)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    private List<String> 지하철역_목록() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }
}
