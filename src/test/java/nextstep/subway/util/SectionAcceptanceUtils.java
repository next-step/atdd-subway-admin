package nextstep.subway.util;

import static nextstep.subway.util.LineAcceptanceUtils.*;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.line.section.SectionCreateRequest;

public class SectionAcceptanceUtils {

	private static final String SECTION_URL = "/sections";

	private SectionAcceptanceUtils() {
		throw new AssertionError("Utility class cannot be instantiated");
	}

	public static ExtractableResponse<Response> 지하철_구간_등록_요청(Long lineId, SectionCreateRequest request) {
		return RestAssuredUtils.post(LINE_URL + "/" +lineId + SECTION_URL, request).extract();
	}
}
