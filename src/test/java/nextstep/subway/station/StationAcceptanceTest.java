package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import nextstep.subway.utils.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends StationAcceptanceTestFixture {

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철역_생성("강남역");

        // then
        TestUtil.응답확인(response, HttpStatus.CREATED);

        // then
        List<String> stationNames = 지하철역_조회().jsonPath().getList("name", String.class);
        assertThat(stationNames).containsAnyOf("강남역");
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
        지하철역_생성("강남역");

        // when
        ExtractableResponse<Response> response = 지하철역_생성("강남역");

        // then
        TestUtil.응답확인(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 지하철역 목록을 응답 받는다
     * Then 생성한 지하철역 2개가 응답받은 지하철역 목록에 존재한다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        List<String> createStations = Arrays.asList("강남역", "교대역");
        createStations.forEach(StationAcceptanceTestFixture::지하철역_생성);

        // when
        ExtractableResponse<Response> response = 지하철역_조회();

        // then
        TestUtil.응답확인(response, HttpStatus.OK);

        // then
        List<String> stationsNames = response.jsonPath().getList("name", String.class);
        createStations.forEach(s -> assertThat(stationsNames.contains(s)).isTrue());
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 지하철역이 삭제된다
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        int stationId = 지하철역_생성("강남역").jsonPath().get("id");

        // when
        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().delete("/stations/" + stationId)
                        .then().log().all()
                        .extract();

        // then
        TestUtil.응답확인(response, HttpStatus.NO_CONTENT);

        // then
        List<String> 지하철_이름_목록 = 지하철역_조회().jsonPath().getList("name", String.class);
        assertThat(지하철_이름_목록.contains("강남역")).isFalse();
    }

}
