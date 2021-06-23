package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class LineStations {

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "fk_line_station_to_line"))
	private final List<LineStation> lineStations = new ArrayList<>();

	public List<LineStation> getStationsInOrder() {
		// 출발지점 찾기
		Optional<LineStation> preLineStation = lineStations.stream()
			.filter(it -> it.getPreStationId() == null)
			.findFirst();

		List<LineStation> result = new ArrayList<>();
		while (preLineStation.isPresent()) {
			LineStation preStationId = preLineStation.get();
			result.add(preStationId);
			preLineStation = lineStations.stream()
				.filter(it -> it.getPreStationId().equals(preStationId.getStationId()))
				.findFirst();
		}
		return result;
	}

	public void add(Line line, Station station) throws LineStationDuplicatedException {
		LineStation lineStation = new LineStation(line, station);
		checkValidation(lineStation);

		lineStations.stream()
			.filter(it -> it.getPreStationId().equals(lineStation.getPreStationId()))
			.findFirst()
			.ifPresent(it -> it.updatePreStationTo(lineStation.getStationId()));

		lineStations.add(lineStation);
	}

	public void removeByStationId(Long stationId) {
		LineStation lineStation = lineStations.stream()
			.filter(it -> it.getStationId().equals(stationId))
			.findFirst()
			.orElseThrow(RuntimeException::new);

		lineStations.stream()
			.filter(it -> it.getPreStationId().equals(stationId))
			.findFirst()
			.ifPresent(it -> it.updatePreStationTo(lineStation.getPreStationId()));

		lineStations.remove(lineStation);
	}

	private void checkValidation(LineStation lineStation) throws LineStationDuplicatedException {
		if (lineStations.stream().anyMatch(it -> it.isSame(lineStation))) {
			throw new LineStationDuplicatedException(lineStation);
		}
	}
}