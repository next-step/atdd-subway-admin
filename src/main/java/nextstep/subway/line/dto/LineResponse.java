package nextstep.subway.line.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LineResponse {
	private Long id;
	private String name;
	private String color;
	private List<StationResponse> stations = new ArrayList<>();
	private LocalDateTime createdDate;
	private LocalDateTime modifiedDate;

	public static LineResponse of(Line line) {
		if (line == null) {
			throw new NotFoundException("노선 정보를 찾을 수 없습니다.");
		}

		return new LineResponse(
			line.getId(),
			line.getName(),
			line.getColor(),
			convertLineToStationResponses(line),
			line.getCreatedDate(),
			line.getModifiedDate());
	}

	private static List<StationResponse> convertLineToStationResponses(Line line) {
		return CollectionUtils.emptyIfNull(line.getAllSection()).stream()
			.map(section -> {
				Station upStation = section.getUpStation();
				Station downStation = section.getDownStation();
				upStation.updateNextDistance(section.getDistance());
				return Arrays.asList(
					upStation,
					downStation
				);
			})
			.flatMap(Collection::stream)
			.distinct()
			.map(StationResponse::of)
			.collect(Collectors.toList());
	}

	public Long findUpStationId() {
		return this.stations.size() > 0 ? this.stations.get(0).getId() : 0L;
	}

	public Long findDownStationId() {
		return this.stations.size() > 0 ? this.stations.get(this.stations.size() - 1).getId() : 0L;
	}

	public int countStations() {
		return this.stations.size();
	}
}
