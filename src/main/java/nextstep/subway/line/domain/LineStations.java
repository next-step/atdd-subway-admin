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

	public List<Integer> getDistancesOnOrder() {
		return getLineStationsInOrder().stream()
			.map(LineStation::getDistance)
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
		values.add(LineStation.of(section.getUpStationId(), null, 0));
		values.add(LineStation.of(section.getDownStationId(), section.getUpStationId(), section.getDistance()));
	}

	private void throwOnBothStationsNotRegistered(Section section) {
		if (!findByStationId(section.getUpStationId()).isPresent() && !findByStationId(
			section.getDownStationId()).isPresent()) {
			throw new SectionAddFailException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.");
		}
	}

	private void throwOnBothStationsAlreadyRegistered(Section section) {
		if (findByStationId(section.getUpStationId()).isPresent() && findByStationId(
			section.getDownStationId()).isPresent()) {
			throw new SectionAddFailException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
		}
	}

	private void updateSection(Section section) {
		if (findByStationId(section.getUpStationId()).isPresent()) {
			updateSectionOnMatchingUpStation(section);
			return;
		}

		if (findByStationId(section.getDownStationId()).isPresent()) {
			updateSectionOnMatchingDownStation(section);
		}
	}

	private void updateSectionOnMatchingUpStation(Section section) {
		Optional<LineStation> optionalNextLineStation = findByPreStationId(section.getUpStationId());
		if (optionalNextLineStation.isPresent()) {
			LineStation nextLineStation = optionalNextLineStation.get();
			throwOnIllegalDistance(nextLineStation.getDistance(), section.getDistance());
			int distance = nextLineStation.getDistance() - section.getDistance();
			nextLineStation.update(section.getDownStationId(), distance);
		}
		values.add(LineStation.of(section.getDownStationId(), section.getUpStationId(), section.getDistance()));
	}

	private void throwOnIllegalDistance(int existingDistance, int newDistance) {
		if (existingDistance <= newDistance) {
			throw new SectionAddFailException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다.");
		}
	}

	private void updateSectionOnMatchingDownStation(Section section) {
		LineStation currentLineStation = findByStationId(section.getDownStationId()).get();

		if (currentLineStation.hasPrev()) {
			throwOnIllegalDistance(currentLineStation.getDistance(), section.getDistance());
		}

		int distance = currentLineStation.hasPrev() ? currentLineStation.getDistance() - section.getDistance() : 0;
		values.add(LineStation.of(section.getUpStationId(), currentLineStation.getPreStationId(), distance));
		currentLineStation.update(section.getUpStationId(), section.getDistance());
	}

	private Optional<LineStation> findByStationId(Long stationId) {
		return values.stream()
			.filter(value -> stationId.equals(value.getStationId()))
			.findFirst();
	}

	private Optional<LineStation> findByPreStationId(Long preStationId) {
		return values.stream()
			.filter(value -> preStationId.equals(value.getPreStationId()))
			.findFirst();
	}
}
