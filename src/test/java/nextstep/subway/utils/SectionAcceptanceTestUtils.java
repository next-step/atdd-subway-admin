package nextstep.subway.utils;

import static nextstep.subway.utils.LineAcceptanceTestUtils.LINE_BASE_URL;

import io.restassured.response.Response;
import nextstep.subway.dto.line.section.CreateSectionRequest;

public class SectionAcceptanceTestUtils {

    public static Response 지하철_구간_생성_요청(final Long lineId, CreateSectionRequest createSectionRequest) {
        return RestAssuredUtils.post(LINE_BASE_URL, lineId, createSectionRequest).extract().response();
    }
}
