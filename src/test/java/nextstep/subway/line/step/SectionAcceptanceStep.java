package nextstep.subway.line.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.dto.StationInLineResponse;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.line.step.LineAcceptanceStep.특정_지하철_노선_조회_요청;
import static org.assertj.core.api.Assertions.assertThat;

public class SectionAcceptanceStep {
    public static ExtractableResponse<Response> 새로운_지하철_구간_추가_요청(
            final Long upStationId, final Long downStationId, final Long distance, final Long lineId
    ) {
        return RestAssured.given().log().all()
                .body(new SectionRequest(upStationId, downStationId, distance))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 새로운_지하철_하행종점역_구간_추가_요청(
            final Long originalEndDownStationId, final Long newEndDownStationId, final Long distance, final Long lineID
    ) {
        return 새로운_지하철_구간_추가_요청(originalEndDownStationId, newEndDownStationId, distance, lineID);
    }

    public static void 지하철_구간내_역_삭제됨(
            final Long lineId, final Long deleteStationId, final int expectedRemainedStationsSize
    ) {
        ExtractableResponse<Response> foundLine = 특정_지하철_노선_조회_요청(lineId);
        LineResponse foundLineResponse = foundLine.as(LineResponse.class);
        List<Long> stationIds = foundLineResponse.getStations().stream()
                .map(StationInLineResponse::getId)
                .collect(Collectors.toList());
        assertThat(stationIds).doesNotContain(deleteStationId);
        assertThat(stationIds).hasSize(expectedRemainedStationsSize);
    }
}
