package nextstep.subway.line.dto;

import java.util.List;
import java.util.stream.Collectors;

import nextstep.subway.line.domain.Line;

public class LineResponses {
	private List<LineResponse> lineResponses;

	public LineResponses(List<LineResponse> lineResponses) {
		this.lineResponses = lineResponses;
	}

	public static LineResponses of(List<Line> lines) {
		return new LineResponses(
			lines.stream()
				.map(LineResponse::of)
				.collect(Collectors.toList())
		);
	}

	public List<LineResponse> get() {
		return lineResponses;
	}
}
