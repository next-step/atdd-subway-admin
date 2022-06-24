package nextstep.subway.utils;

import static io.restassured.RestAssured.delete;
import static nextstep.subway.utils.LineAcceptanceTestUtils.LINE_BASE_URL;
import static nextstep.subway.utils.RestAssuredUtils.post;

import io.restassured.response.Response;
import nextstep.subway.dto.line.section.CreateSectionRequest;

public class SectionAcceptanceTestUtils {

    public static final String LINE_SECTION_BASE_URL = LINE_BASE_URL.concat("/%d/sections");
    public static final String ADD_LINE_SECTION_BASE_URL = LINE_BASE_URL.concat("/{id}/sections");
    public static final String REMOVE_LINE_SECTION_BASE_URL = ADD_LINE_SECTION_BASE_URL.concat("?stationId={stationId}");

    public static Response 지하철_구간_생성_요청(final Long lineId, CreateSectionRequest createSectionRequest) {
        return post(makeLineSectionUrlTemplate(lineId), createSectionRequest).extract().response();
    }

    public static Response 지하철_구간_삭제_요청(Long lineId, Long stationId) {
        return delete(REMOVE_LINE_SECTION_BASE_URL, lineId, stationId);
    }

    private static String makeLineSectionUrlTemplate(Long lineId) {
        return String.format(LINE_SECTION_BASE_URL, lineId);
    }
}
