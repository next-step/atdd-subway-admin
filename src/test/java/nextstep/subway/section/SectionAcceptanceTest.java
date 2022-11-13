package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.DatabaseCleanup;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_생성;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }
        databaseCleanup.execute();
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 지하철 구간을 추가하면
     * Then 노선에 새로운 지하철 역이 등록된다
     */
    @DisplayName("노선 중간에 지하철 구간을 추가한다.")
    @Test
    void addSection() {
        // given
        int distance = 10;
        String expectLine = "신분당선";
        Long upStationId = StationAcceptanceTest.지하철_역_생성("판교역")
                .jsonPath().getLong("id");
        Long downStationId = StationAcceptanceTest.지하철_역_생성("강남역")
                .jsonPath().getLong("id");
        Long lineId = 지하철_노선_생성(expectLine, "주황색", upStationId, downStationId, distance)
                .jsonPath().getLong("id");
        Long newStationId = StationAcceptanceTest.지하철_역_생성("양재역")
                .jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_구간_추가(lineId, newStationId, downStationId, 5);

        // then
        List<String> stationNames = response
                .jsonPath()
                .getList("stations.name", String.class);
        assertThat(stationNames).containsOnly("판교역", "양재역", "강남역");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 상행성 종점에 지하철 구간을 추가하면
     * Then 노선에 새로운 지하철 역이 등록된다
     */
    @DisplayName("상행 종점에 지하철 구간을 추가한다.")
    @Test
    void addUpSection() {
        // given
        int distance = 10;
        String expectLine = "신분당선";
        Long upStationId = StationAcceptanceTest.지하철_역_생성("판교역")
                .jsonPath().getLong("id");
        Long downStationId = StationAcceptanceTest.지하철_역_생성("강남역")
                .jsonPath().getLong("id");
        Long lineId = 지하철_노선_생성(expectLine, "주황색", upStationId, downStationId, distance)
                .jsonPath().getLong("id");
        Long newStationId = StationAcceptanceTest.지하철_역_생성("양재역")
                .jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_구간_추가(lineId, newStationId, upStationId, 5);

        // then
        List<String> stationNames = response
                .jsonPath()
                .getList("stations.name", String.class);
        assertThat(stationNames).containsOnly("판교역", "양재역", "강남역");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 하행선 종점에 지하철 구간을 추가하면
     * Then 노선에 새로운 지하철 역이 등록된다
     */
    @DisplayName("하행 종점에 지하철 구간을 추가한다.")
    @Test
    void addDownSection() {
        // given
        int distance = 10;
        String expectLine = "신분당선";
        Long upStationId = StationAcceptanceTest.지하철_역_생성("판교역")
                .jsonPath().getLong("id");
        Long downStationId = StationAcceptanceTest.지하철_역_생성("강남역")
                .jsonPath().getLong("id");
        Long lineId = 지하철_노선_생성(expectLine, "주황색", upStationId, downStationId, distance)
                .jsonPath().getLong("id");
        Long newStationId = StationAcceptanceTest.지하철_역_생성("양재역")
                .jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_구간_추가(lineId, downStationId, newStationId, 5);

        // then
        List<String> stationNames = response
                .jsonPath()
                .getList("stations.name", String.class);
        assertThat(stationNames).containsOnly("판교역", "양재역", "강남역");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 추가하는 지하철 구간의 거리가 기존구간과 동일하거나 클 경우
     * Then 새로운 지하철 역을 추가할 수 없다
     */
    @DisplayName("구간 거리가 동일하거나 더 클 경우 역을 추가할 수 없다")
    @ParameterizedTest
    @ValueSource(ints = {10, 11})
    void addSectionException(int newDistance) {
        // given
        int distance = 10;
        String expectLine = "신분당선";
        Long upStationId = StationAcceptanceTest.지하철_역_생성("판교역")
                .jsonPath().getLong("id");
        Long downStationId = StationAcceptanceTest.지하철_역_생성("강남역")
                .jsonPath().getLong("id");
        Long lineId = 지하철_노선_생성(expectLine, "주황색", upStationId, downStationId, distance)
                .jsonPath().getLong("id");
        Long newStationId = StationAcceptanceTest.지하철_역_생성("양재역")
                .jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_구간_추가(lineId, newStationId, downStationId, newDistance);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 상행역, 하행역 모두 노선에 등록되어 있다면
     * Then 새로운 지하철 역을 추가할 수 없다
     */
    @DisplayName("상행역, 하행역 모두 존재할 경우 추가할 수 없다")
    @Test
    void addExistSectionException() {
        // given
        int distance = 10;
        String expectLine = "신분당선";
        Long upStationId = StationAcceptanceTest.지하철_역_생성("판교역")
                .jsonPath().getLong("id");
        Long downStationId = StationAcceptanceTest.지하철_역_생성("강남역")
                .jsonPath().getLong("id");
        Long lineId = 지하철_노선_생성(expectLine, "주황색", upStationId, downStationId, distance)
                .jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_구간_추가(lineId, upStationId, downStationId, 4);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 추가하는 상행역, 하행역 모두 존재하지 않으면
     * Then 새로운 지하철 역을 추가할 수 없다
     */
    @DisplayName("상행역, 하행역 모두 존재하지 않는 경우 추가할 수 없다")
    @Test
    void addNotExistSectionException() {
        // given
        int distance = 10;
        Long upStationId = StationAcceptanceTest.지하철_역_생성("판교역")
                .jsonPath().getLong("id");
        Long downStationId = StationAcceptanceTest.지하철_역_생성("강남역")
                .jsonPath().getLong("id");
        Long lineId = 지하철_노선_생성("신분당선", "주황색", upStationId, downStationId, distance)
                .jsonPath().getLong("id");
        Long newStationId1 = StationAcceptanceTest.지하철_역_생성("양재역")
                .jsonPath().getLong("id");
        Long newStationId2 = StationAcceptanceTest.지하철_역_생성("양재시민의숲")
                .jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_구간_추가(lineId, newStationId1, newStationId2, 4);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 지하철_구간_추가(
            Long lineId,
            Long upStationId,
            Long downStationId,
            int distance
    ) {
        LineRequest request = new LineRequest.Builder()
                .upStationId(upStationId)
                .downStationId(downStationId)
                .distance(distance)
                .build();

        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_구간_전체_조회() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_구간_조회(Long id) {
        return RestAssured.given().log().all()
                .when().get("/lines/" + id)
                .then().log().all()
                .extract();
    }
}
