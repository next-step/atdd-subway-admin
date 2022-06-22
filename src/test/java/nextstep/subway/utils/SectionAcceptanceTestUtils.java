package nextstep.subway.utils;

import static nextstep.subway.utils.LineAcceptanceTestUtils.LINE_BASE_URL;

import io.restassured.response.Response;
import nextstep.subway.dto.line.section.CreateSectionRequest;

public class SectionAcceptanceTestUtils {

    public static final String LINE_SECTION_BASE_URL = LINE_BASE_URL.concat("/%d/sections");

    public static Response 지하철_구간_생성_요청(final Long lineId, CreateSectionRequest createSectionRequest) {
        return RestAssuredUtils.post(makeLineSectionUrlTemplate(lineId), createSectionRequest).extract().response();
    }

    private static String makeLineSectionUrlTemplate(Long lineId) {
        return String.format(LINE_SECTION_BASE_URL, lineId);
    }
}
