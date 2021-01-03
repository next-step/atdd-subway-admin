package nextstep.subway.line.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

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
	@NotBlank
	private String upStationId;
	@NotBlank
	private String downStationId;
	@Min(1)
	private String distance;

	public LineRequest(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public Line toLine() {
		return new Line(name, color);
	}

	public Line toLine(Station upStation, Station downStation) {
		return new Line(name, color, upStation, downStation, Integer.parseInt(distance));
	}
}
