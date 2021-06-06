package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

import java.util.List;
import java.util.stream.Collectors;

public class LineAcceptanceTestUtils {

	static LineRequest makeLineRequest(final String name, final String color) {
		return new LineRequest(name, color);
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
