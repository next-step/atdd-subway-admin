package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.StationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;


@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @TestFactory
    Stream<DynamicTest> createStation() {
        return Stream.of(
            dynamicTest("지하철역을 생성하는 요청으로 새로운 지하철역을 생성", () -> {
                ExtractableResponse<Response> response = postStation(generateStationRequest("강남역"));
                assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            }),

            dynamicTest("새로 생성한 지하철역을 조회", () -> {
                List<String> stationNames = showStationNames();
                assertThat(stationNames).containsAnyOf("강남역");
            })
        );
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @TestFactory
    Stream<DynamicTest> createStationWithDuplicateName() {
        return Stream.of(
            dynamicTest("지하철역을 생성하는 요청으로 새로운 지하철역을 생성", () -> {
                ExtractableResponse<Response> response = postStation(generateStationRequest("강남역"));
                assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            }),

            dynamicTest("지하철역을 기존 생성했던 동일 이름으로 생성하면 예외 발생", () -> {
                ExtractableResponse<Response> response = postStation(generateStationRequest("강남역"));
                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            })
        );
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
        postStation(generateStationRequest("강남역"));
        postStation(generateStationRequest("서초역"));

        // when
        List<String> stationNames = showStationNames();

        // then
        assertThat(stationNames).hasSize(2);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @TestFactory
    Stream<DynamicTest> deleteStation() {
        final long[] deleteTargetStationId = new long[1];
        final String[] deleteTargetStationName = new String[1];

        return Stream.of(
                dynamicTest("지하철역을 생성하는 요청으로 새로운 지하철역을 생성", () -> {
                    ExtractableResponse saveResponse = postStation(generateStationRequest("강남역"));
                    assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

                    deleteTargetStationId[0] = saveResponse.body().jsonPath().getLong("id");
                    deleteTargetStationName[0] = saveResponse.body().jsonPath().getString("name");
                }),

                dynamicTest("생성한 지하철역 삭제 후 조회하면 해당 지하철역은 조회되지 않음", () -> {
                    deleteStation(deleteTargetStationId[0]);
                    List<String> stationNames = showStationNames();
                    assertThat(stationNames).doesNotContain(deleteTargetStationName[0]);
                })
        );
    }

    private ExtractableResponse postStation(StationRequest params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    private List<String> showStationNames() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }

    private void deleteStation(long deleteTargetStationId) {
        RestAssured.given().log().all()
                .body(generateStationRequest("강남역"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/stations" + DELIMITER + deleteTargetStationId)
                .then().log().all();
    }

    public StationRequest generateStationRequest(String name) {
        return new StationRequest(name);
    }
}
