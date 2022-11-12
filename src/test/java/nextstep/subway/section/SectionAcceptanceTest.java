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
    @DisplayName("노선에 지하철 구간을 추가한다.")
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
        assertThat(stationNames).contains("판교역", "양재역", "강남역");
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
                .statusCode(HttpStatus.CREATED.value())
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
