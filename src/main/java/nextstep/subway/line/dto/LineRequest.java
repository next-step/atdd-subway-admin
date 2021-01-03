package nextstep.subway.line.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Getter
@AllArgsConstructor
public class LineRequest {
	@NotBlank
	private String name;
	@NotBlank
	private String color;
	@NotNull
	private Long upStationId;
	@NotNull
	private Long downStationId;
	@Min(1)
	private int distance;

	public Line toLine() {
		return new Line(name, color);
	}

	public Line toLine(Station upStation, Station downStation) {
		return new Line(name, color, upStation, downStation, distance);
	}
}
