package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AssertUtils;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.line.LineRestAssured;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationRestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends BaseAcceptanceTest {

    @DisplayName("지하철 구간 등록")
    @TestFactory
    Stream<DynamicNode> addSection() {
        long 강남역_id = toStationId(StationRestAssured.createStation("강남역"));
        long 양재역_id = toStationId(StationRestAssured.createStation("양재역"));
        long 양재시민의숲역_id = toStationId(StationRestAssured.createStation("양재시민의숲역"));
        long 청계산입구역_id = toStationId(StationRestAssured.createStation("청계산입구역"));
        long 판교역_id = toStationId(StationRestAssured.createStation("판교역"));
        long 정자역_id = toStationId(StationRestAssured.createStation("정자역"));
        long 미금역_id = toStationId(StationRestAssured.createStation("미금역"));
        long 신분당선_id = toLineId(LineRestAssured.callCreateLine("신분당선", "bg-red-600", 양재시민의숲역_id,
                                                                   판교역_id, 10));
        return Stream.of(
                // 정상 케이스
                dynamicTest("역 사이에 새로운 역을 등록", () -> {
                    callAddSection(신분당선_id, 양재시민의숲역_id, 청계산입구역_id, 5);

                    ExtractableResponse<Response> response = LineRestAssured.callGetLines();
                    assertThat(toStationNames(response)).containsExactly("양재시민의숲역", "청계산입구역", "판교역");
                }),
                dynamicTest("새로운 역을 상행 종점으로 등록", () -> {
                    callAddSection(신분당선_id, 양재역_id, 양재시민의숲역_id, 5);

                    ExtractableResponse<Response> response = LineRestAssured.callGetLines();
                    assertThat(toStationNames(response)).containsExactly("양재역", "양재시민의숲역", "청계산입구역",
                                                                         "판교역");
                }),
                dynamicTest("새로운 역을 하행 종점으로 등록", () -> {
                    callAddSection(신분당선_id, 판교역_id, 미금역_id, 5);

                    ExtractableResponse<Response> response = LineRestAssured.callGetLines();
                    assertThat(toStationNames(response)).containsExactly("양재역", "양재시민의숲역", "청계산입구역",
                                                                         "판교역", "미금역");
                }),
                // 예외 케이스
                dynamicTest("기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음", () -> {
                    ExtractableResponse<Response> response = callAddSection(신분당선_id, 정자역_id, 미금역_id, 5);

                    AssertUtils.assertStatusCode(response, HttpStatus.BAD_REQUEST);
                }),
                dynamicTest("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음", () -> {
                    ExtractableResponse<Response> response = callAddSection(신분당선_id, 양재역_id, 미금역_id, 5);

                    AssertUtils.assertStatusCode(response, HttpStatus.BAD_REQUEST);
                }),
                dynamicTest("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음", () -> {
                    ExtractableResponse<Response> response = callAddSection(신분당선_id, 강남역_id, 정자역_id, 5);

                    AssertUtils.assertStatusCode(response, HttpStatus.BAD_REQUEST);
                })
        );
    }

    private ExtractableResponse<Response> callAddSection(long lineId, long upStationId, long downStationId,
                                                         long distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();
    }

    private long toStationId(ExtractableResponse<Response> response) {
        return AssertUtils.toId(response);
    }

    private long toLineId(ExtractableResponse<Response> response) {
        return AssertUtils.toId(response);
    }

    private List<String> toStationNames(ExtractableResponse<Response> response) {
        return response.body()
                .jsonPath()
                .getList(".", LineResponse.class)
                .stream()
                .flatMap(lineResponse -> lineResponse.getStations().stream())
                .map(stationResponse -> stationResponse.getName())
                .collect(Collectors.toList());
    }
}
