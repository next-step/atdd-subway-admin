package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.CommonMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

public class SectionAcceptanceTest {

    private static final String URL = "/lines";

    private Long startStationID;
    private Long endStationId;
    private Long lineId;
    private int originalDistance;

    void setUp() {
        startStationID = StationAcceptanceTest
            .createStation("신도림역")
            .jsonPath()
            .getObject(".", StationResponse.class)
            .getId();

        endStationId = StationAcceptanceTest
            .createStation("잠실역")
            .jsonPath()
            .getObject(".", StationResponse.class)
            .getId();
        originalDistance = 10;
        lineId = getIdWithResponse(LineAcceptanceTest
            .createLine("2호선",
                "green",
                startStationID,
                endStationId,
                originalDistance)
        );
    }

    @DisplayName("노선에 지하철역을 추가한다.")
    @ParameterizedTest
    @MethodSource("section")
    void add_station_to_line(Long upStationId, Long downStationId, int distance) {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = addSection(upStationId, downStationId, distance);

        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private Stream<Arguments> section() {
        Long middleStationId = StationAcceptanceTest
            .createStation("신촌역")
            .jsonPath()
            .getObject(".", StationResponse.class)
            .getId();
        Long firstStationId = StationAcceptanceTest
            .createStation("까치산역")
            .jsonPath()
            .getObject(".", StationResponse.class)
            .getId();
        Long lastStationId = StationAcceptanceTest
            .createStation("신설동역")
            .jsonPath()
            .getObject(".", StationResponse.class)
            .getId();

        return Stream.of(
            Arguments.of(startStationID, middleStationId, 4),
            Arguments.of(firstStationId, startStationID, 5),
            Arguments.of(endStationId, lastStationId, 7)
        );
    }

    private ExtractableResponse<Response> addSection(Long upStationId, Long downStationId,
        int distance) {
        Map<String, Object> params = body(upStationId, downStationId, distance);
        return CommonMethod.create(params, URL + "/" + lineId + "/sections");
    }

    private Map<String, Object> body(Long upStationId, Long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return params;
    }

    private Long getIdWithResponse(ExtractableResponse<Response> response) {
        return response.jsonPath().getObject(".", LineResponse.class).getId();
    }

}
