package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class LineStations {
	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<LineStation> lineStations = new ArrayList<>();

	public List<Station> getStations() {
		return lineStations.stream()
			.map(LineStation::getStation)
			.collect(Collectors.toList());
	}

	public void addStation(Line line, Station station, Station downStation, int distance) {
		if (station == null || downStation == null) {
			return;
		}
		LineStation upLineStation = new LineStation.Builder()
			.line(line)
			.station(station)
			.downStation(downStation)
			.distance(distance)
			.build();
		LineStation downLineStation = new LineStation.Builder()
			.line(line)
			.station(downStation)
			.build();
		addStation(upLineStation, downLineStation);
	}

	private void addStation(LineStation upLineStation, LineStation downLineStation) {
		checkValidation(upLineStation, downLineStation);
		int upStationIndex = lineStations.indexOf(upLineStation);
		int downStationIndex = lineStations.indexOf(downLineStation);
		if (upStationIndex != -1 && lineStations.get(upStationIndex).getDownStation() != null) {
			addBetweenTwoOnLeftSide(lineStations.get(upStationIndex), upLineStation, downLineStation);
			return;
		}
		if (downStationIndex != -1 && lineStations.get(downStationIndex).getDownStation() == null) {
			addBetweenTwoOnRightSide(upLineStation, downLineStation);
			return;
		}
		add(upLineStation, downLineStation);
	}

	private void checkValidation(LineStation upLineStation, LineStation downLineStation) {
		int upStationIndex = lineStations.indexOf(upLineStation);
		int downStationIndex = lineStations.indexOf(downLineStation);
		if (upLineStation.equals(downLineStation)) {
			throw new IllegalArgumentException("upStation과 downStation은 동일할 수 없습니다.");
		}
		if (upStationIndex != -1 && downStationIndex != -1) {
			throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
		}
		if (!lineStations.isEmpty() && upStationIndex == -1 && downStationIndex == -1) {
			throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.");
		}
	}

	private void addBetweenTwoOnRightSide(LineStation center, LineStation right) {
		LineStation left = searchUpLineStation(right);
		if (left.getDistance() <= center.getDistance()) {
			throw new IllegalArgumentException("기존 역 사이 길이보다 크거나 같을 수 없습니다.");
		}
		left.changeDownStation(center.getStation());
		left.changeDistance(left.getDistance() - center.getDistance());
		overwrite(left);
		add(center, right);
	}

	private LineStation searchUpLineStation(LineStation downLineStation) {
		return lineStations.stream()
			.filter(lineStation -> downLineStation.getStation().equals(lineStation.getDownStation()))
			.findAny()
			.orElseThrow(() -> new RuntimeException("노선에 속해있지만 어느 구간에도 포함되어있지 않은 지하철역이 존재합니다."));
	}

	private void addBetweenTwoOnLeftSide(LineStation oldLeft, LineStation newLeft, LineStation center) {
		if (oldLeft.getDistance() <= newLeft.getDistance()) {
			throw new IllegalArgumentException("기존 역 사이 길이보다 크거나 같을 수 없습니다.");
		}
		center.changeDownStation(oldLeft.getDownStation());
		center.changeDistance(oldLeft.getDistance() - newLeft.getDistance());
		add(newLeft, center);
	}

	private void add(LineStation upLineStation, LineStation downLineStation) {
		overwrite(upLineStation);
		addIfNotExist(downLineStation);
	}

	private void overwrite(LineStation lineStation) {
		int index = lineStations.indexOf(lineStation);
		if (index == -1) {
			lineStations.add(lineStation);
			return;
		}
		lineStations.set(index, lineStation);
	}

	private void addIfNotExist(LineStation lineStation) {
		if (lineStations.contains(lineStation)) {
			return;
		}
		lineStations.add(lineStation);
	}

	public void removeStation(Line line, Station station) {
		lineStations.remove(LineStation.of(line, station));
	}

	public int getDistance(Station station1, Station station2) {
		return lineStations.stream()
			.filter(lineStation ->
				station1.equals(lineStation.getStation()) && station2.equals(lineStation.getDownStation())
					|| station2.equals(lineStation.getStation()) && station1.equals(lineStation.getDownStation()))
			.max(Comparator.comparingInt(LineStation::getDistance))
			.map(LineStation::getDistance)
			.orElse(0);
	}
}
