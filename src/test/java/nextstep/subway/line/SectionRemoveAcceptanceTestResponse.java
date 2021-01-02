package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionRemoveAcceptanceTestResponse {
    public static void 지하철_노선에_지하철역_제거됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.as(LineResponse.class).getStations()).hasSize(2);
    }

    public static void 노선에_지하철역_제거된_목록_정렬됨(ExtractableResponse<Response> response, List<Long> StationIds) {
        List<Long> sortedStationIds = response.as(LineResponse.class).getStations()
                .stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(sortedStationIds).containsExactlyElementsOf(StationIds);
    }
}
