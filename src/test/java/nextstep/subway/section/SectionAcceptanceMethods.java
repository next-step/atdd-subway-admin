package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceMethodsTestFixture;
import nextstep.subway.section.dto.SectionRequest;

import static nextstep.subway.line.LineAcceptanceMethods.LINE_PATH;

public class SectionAcceptanceMethods extends AcceptanceMethodsTestFixture {

    public static final String SECTION_PATH = "/sections";

    private SectionAcceptanceMethods() {
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철역_등록(Long lineId, SectionRequest sectionRequest) {
        return post(LINE_PATH + SLASH + lineId + SECTION_PATH, sectionRequest);
    }
}
