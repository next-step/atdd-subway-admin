package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class LineStations {
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_line_station_to_line"))
	private List<LineStation> values = new ArrayList<>();

	protected LineStations() {

	}

	private LineStations(List<LineStation> values) {
		this.values = values;
	}

	public static LineStations of(List<LineStation> values) {
		return new LineStations(values);
	}

	public List<LineStation> getLineStationsInOrder() {
		List<LineStation> result = new ArrayList<>();

		Optional<LineStation> optionalCurrentLineStation = getFirstLineStation();
		while (optionalCurrentLineStation.isPresent()) {
			LineStation currentLineStation = optionalCurrentLineStation.get();
			result.add(currentLineStation);
			optionalCurrentLineStation = values.stream()
				.filter(lineStation -> currentLineStation.getStationId().equals(lineStation.getPreStationId()))
				.findFirst();
		}

		return result;
	}

	public List<Long> getStationIdsInOrder() {
		return getLineStationsInOrder().stream()
			.map(LineStation::getStationId)
			.collect(Collectors.toList());
	}

	private Optional<LineStation> getFirstLineStation() {
		return values.stream()
			.filter(lineStation -> lineStation.getPreStationId() == null)
			.findFirst();
	}

	public int size() {
		return values.size();
	}

	public void addSection(Section section) {
		if (isEmpty()) {
			addFirstSection(section);
			return;
		}

		throwOnBothStationsNotRegistered(section);
		throwOnBothStationsAlreadyRegistered(section);

		updateSection(section);
	}

	private boolean isEmpty() {
		return values.isEmpty();
	}

	private void addFirstSection(Section section) {
		values.add(LineStation.of(section.getUpStation(), null, 0));
		values.add(LineStation.of(section.getDownStation(), section.getUpStation(), section.getDistance()));
	}

	private void throwOnBothStationsNotRegistered(Section section) {
		if (!contains(section.getUpStation()) && !contains(section.getDownStation())) {
			throw new SectionAddFailException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.");
		}
	}

	private void throwOnBothStationsAlreadyRegistered(Section section) {
		if (contains(section.getUpStation()) && contains(section.getDownStation())) {
			throw new SectionAddFailException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
		}
	}

	private boolean contains(Station station) {
		return getStationIds().contains(station.getId());
	}

	private List<Long> getStationIds() {
		return values.stream()
			.map(LineStation::getStationId)
			.collect(Collectors.toList());
	}

	private void updateSection(Section section) {
		// TODO
	}
}
