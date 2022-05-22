package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends BaseAcceptanceTest {

    @BeforeEach
    void setup(@Autowired StationRepository stationRepository) {
        stationRepository.deleteAll();
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
        ExtractableResponse<Response> response = StationRestAssured.createStation("강남역");

        // then
        assertStatusCode(response, HttpStatus.CREATED);

        // then
        List<String> stationNames = toStationNames(callGetStations().extract());
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
        StationRestAssured.createStation("강남역");

        // when
        ExtractableResponse<Response> response = StationRestAssured.createStation("강남역");

        // then
        assertStatusCode(response, HttpStatus.BAD_REQUEST);
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
        StationRestAssured.createStation("강남역");
        StationRestAssured.createStation("잠실역");

        // when
        ExtractableResponse<Response> response = callGetStations().extract();

        // then
        assertAll(
                () -> assertStatusCode(response, HttpStatus.OK),
                () -> assertThat(toStationNames(response)).containsAnyOf( "강남역", "잠실역")
        );
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
        long id = StationRestAssured.createStation("강남역")
                                    .jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                                                            .when().delete("/stations/{id}", id)
                                                            .then().log().all()
                                                            .extract();

        // then
        assertStatusCode(response, HttpStatus.NO_CONTENT);

        // then
        List<String> stationNames = toStationNames(callGetStations().extract());
        assertThat(stationNames).doesNotContain("강남역");
    }

    private static ValidatableResponse callGetStations() {
        return RestAssured.given().log().all()
                          .when().get("/stations")
                          .then().log().all();
    }

    private static void assertStatusCode(ExtractableResponse<Response> response, HttpStatus httpStatus) {
        assertThat(response.statusCode()).isEqualTo(httpStatus.value());
    }

    private static List<String> toStationNames(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("name", String.class);
    }
}
