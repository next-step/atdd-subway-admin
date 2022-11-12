package nextstep.subway.station;

import static nextstep.subway.station.CreateFactory.지하철역_등록_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AbstractAcceptanceTest {

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        String stationName = "교대역";
        ExtractableResponse<Response> response = 지하철역_등록_요청(stationName);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = 지하철역_목록_조회_요청().jsonPath().getList("name", String.class);
        assertThat(stationNames).containsAnyOf(stationName);
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
        String duplicateStationName = "강남역";
        지하철역_등록_요청(duplicateStationName);

        // when
        ExtractableResponse<Response> response = 지하철역_등록_요청(duplicateStationName);

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
        지하철역_등록_요청("상수역");
        지하철역_등록_요청("역삼역");

        // when
        ExtractableResponse<Response> response = 지하철역_목록_조회_요청();

        // then
        assertThat(response.jsonPath().getList("name", String.class)).hasSize(2);
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
        String stationName = "선릉역";
        ExtractableResponse<Response> createStationResponse = 지하철역_등록_요청(stationName);
        Long id = createStationResponse.jsonPath().getLong("id");

        // when
        지하철역_삭제_요청(id);

        // then
        List<String> stationNames = 지하철역_목록_조회_요청().jsonPath().getList("name", String.class);
        assertThat(stationNames).doesNotContain(stationName);
    }

    private ExtractableResponse<Response> 지하철역_목록_조회_요청() {
        return  RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철역_삭제_요청(Long id) {
        return  RestAssured.given().log().all()
                .pathParam("id", id)
                .when().delete("/stations/{id}")
                .then().log().all()
                .extract();
    }
}
