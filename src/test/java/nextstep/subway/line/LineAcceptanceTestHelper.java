package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

public class LineAcceptanceTestHelper {

	private LineAcceptanceTestHelper() {
		// empty
	}

	static LineRequest createLineRequest(final String name, final String color, final StationResponse upStation, final StationResponse downStation, final int distance) {
		return new LineRequest(name, color, upStation.getId(), downStation.getId(), distance);
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
