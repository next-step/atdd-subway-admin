package nextstep.subway.line;

import static nextstep.subway.AcceptanceTest.post;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.HttpStatus;

public class LineAddSectionAcceptanceTestMethods {

    private static final String LINE_WITH_SECTION_URL_PATH_FORMAT = "/lines/%s/sections";

    public static ExtractableResponse<Response> 지하철_노선에_새로운_구간_추가(Long lineId, SectionRequest sectionRequest) {
        return post(String.format(LINE_WITH_SECTION_URL_PATH_FORMAT, lineId), sectionRequest);
    }

    public static void 지하철_노선에_새로운_구간_추가됨(ExtractableResponse response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선에_추가되지_않음(ExtractableResponse response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 지하철_역_정렬됨(ExtractableResponse response, List<Long> stationIds) {
        List<StationResponse> stationResponses = response.jsonPath().getList("stations", StationResponse.class);
        List<Long> stationResponseIds = stationResponses.stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());

        assertThat(stationResponseIds).isEqualTo(stationIds);
    }
}
