package nextstep.subway.line.domain;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

@Entity
public class Line extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String name;
	private String color;
	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	private final Set<LineStation> lineStations = new HashSet<>();

	public Line() {
	}

	public Line(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public static Line of(String name, String color, Station upStation, Station downStation, int distance) {
		Line line = new Line(name, color);
		line.addOrUpdateStation(upStation, downStation, distance);
		return line;
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

	public void update(Line line) {
		this.name = line.getName();
		this.color = line.getColor();
	}

	public void addOrUpdateStation(Station station, Station downStation, int distance) {
		if(station == null) {
			return;
		}
		if(downStation != null) {
			addOrUpdateStation(downStation, null, 0);
		}
		LineStation lineStation = new LineStation.Builder()
			.line(this)
			.station(station)
			.downStation(downStation)
			.distance(distance)
			.build();
		if (!lineStations.add(lineStation)) {
			lineStations.remove(lineStation);
			lineStations.add(lineStation);
		}
	}

	public void removeStation(Station station) {
		lineStations.remove(LineStation.of(this, station));
	}

	public void removeAll() {
		lineStations.clear();
	}

	public List<Station> getStations() {
		return lineStations.stream()
			.map(LineStation::getStation)
			.collect(Collectors.toList());
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
