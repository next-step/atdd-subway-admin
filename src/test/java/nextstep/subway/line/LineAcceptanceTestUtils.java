package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LineAcceptanceTestUtils {

	static Map<String, Object> makeBaseParams(final String name, final String color) {
		return makeParams(name, color, 0l, 0l, 0);
	}

	static Map<String, Object> makeParams(final String name, final String color, final Long upStationId, final Long downStationId, final int distance) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);
		params.put("upStationId", upStationId);
		params.put("downStationId", downStationId);
		params.put("distance", distance);
		return params;
	}

	static LineResponse convertToLineResponse(ExtractableResponse<Response> response) {
		return response.jsonPath().getObject(".", LineResponse.class);
	}

	static List<LineResponse> convertToLineResponses(ExtractableResponse<Response> response) {
		return response.jsonPath().getList(".", LineResponse.class);
	}

	static boolean containsToResponse(final List<LineResponse> expected, final List<LineResponse> actual) {
		List<Long> actualIds = actual.stream().map(LineResponse::getId).collect(Collectors.toList());
		return expected.stream()
					   .map(LineResponse::getId)
					   .anyMatch(actualIds::contains);
	}
}
