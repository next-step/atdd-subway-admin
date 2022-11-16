package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceMethodsTestFixture;
import nextstep.subway.section.dto.SectionRequest;

import static nextstep.subway.line.LineAcceptanceMethods.LINE_PATH;

public class SectionAcceptanceMethods extends AcceptanceMethodsTestFixture {

    private static final String SECTION_PATH = "/sections";
    private static final String REQUEST_PARAM_STATION_ID = "?stationId=";

    private SectionAcceptanceMethods() {
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철역_등록(Long lineId, SectionRequest sectionRequest) {
        return post(LINE_PATH + SLASH + lineId + SECTION_PATH, sectionRequest);
    }

    public static ExtractableResponse<Response> 지하철_노선에서_지하철역_제거(Long lineId, Long stationId) {
        return delete(LINE_PATH + SLASH + lineId + SECTION_PATH + REQUEST_PARAM_STATION_ID + stationId);
    }
}
