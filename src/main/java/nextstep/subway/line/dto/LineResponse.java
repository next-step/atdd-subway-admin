package nextstep.subway.line.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import nextstep.subway.common.dto.BaseResponse;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.dto.StationResponse;

public class LineResponse extends BaseResponse {
	private Long id;
	private String name;
	private String color;
	private List<StationResponse> stations;

	protected LineResponse(Long id, String name, String color,
		List<StationResponse> stations, LocalDateTime createdDate, LocalDateTime modifiedDate) {
		super(createdDate, modifiedDate);
		this.id = id;
		this.name = name;
		this.color = color;
		this.stations = stations;
	}

	public static LineResponse of(Line line) {
		List<StationResponse> stations = line.stations().stream()
			.map(StationResponse::of)
			.collect(Collectors.toList());
		return new LineResponse(line.id(), line.name(), line.color(), stations, line.createdDate(),
			line.modifiedDate());
	}

	public static List<LineResponse> ofList(List<Line> lines) {
		return lines.stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	public List<StationResponse> getStations() {
		return stations;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof LineResponse)) {
			return false;
		}
		LineResponse that = (LineResponse)object;
		return Objects.equals(id, that.id) && Objects.equals(name, that.name)
			&& Objects.equals(color, that.color) && Objects.equals(stations, that.stations);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, color, stations);
	}
}
