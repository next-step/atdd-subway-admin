package nextstep.subway.line.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.station.dto.StationResponse;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LineResponse {
	private Long id;
	private String name;
	private String color;
	private List<StationResponse> stations = new ArrayList<>();
	private LocalDateTime createdDate;
	private LocalDateTime modifiedDate;

	public LineResponse(Long id, String name, String color, LocalDateTime createdDate, LocalDateTime modifiedDate) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}

	public static LineResponse of(Line line) {
		if (line == null) {
			throw new LineNotFoundException("노선 정보를 찾을 수 없습니다.");
		}
		return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getCreatedDate(),
			line.getModifiedDate());
	}

	public Long findUpStationId() {
		return this.stations.size() > 0 ? this.stations.get(0).getId() : 0L;
	}

	public Long findDownStationId() {
		return this.stations.size() > 0 ? this.stations.get(this.stations.size() - 1).getId() : 0L;
	}
}
