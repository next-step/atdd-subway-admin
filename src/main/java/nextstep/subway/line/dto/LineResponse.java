package nextstep.subway.line.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.dto.StationResponse;

@AllArgsConstructor
@Getter
public class LineResponse {
	private Long id;
	private String name;
	private String color;
	private List<StationResponse> stations;
	private LocalDateTime createdDate;
	private LocalDateTime modifiedDate;

	public static LineResponse of(Line line) {
		List<StationResponse> stationResponses = line.Stations().stream()
			.map(StationResponse::of)
			.collect(Collectors.toList());

		return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses,
			line.getCreatedDate(), line.getModifiedDate());
	}
}
