package nextstep.subway.line.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

public class LineResponse {
	private Long id;
	private String name;
	private String color;
	private List<Station> stations;
	private LocalDateTime createdDate;
	private LocalDateTime modifiedDate;

	protected LineResponse(Long id, String name, String color,
		List<Station> stations, LocalDateTime createdDate, LocalDateTime modifiedDate) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.stations = stations;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}

	public static LineResponse of(Line line) {
		return new LineResponse(line.id(), line.name(), line.color(), line.stations(), line.createdDate(),
			line.modifiedDate());
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

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
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
			&& Objects.equals(color, that.color) && Objects.equals(createdDate, that.createdDate)
			&& Objects.equals(modifiedDate, that.modifiedDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, color, createdDate, modifiedDate);
	}
}
