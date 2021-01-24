package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
	private final List<Section> sections = new ArrayList<>();

	protected Line() {
	}

	public Line(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public Line(String name, String color, Station upStation, Station downStation, int distance) {
		this.name = name;
		this.color = color;
		sections.add(new Section(this, upStation, downStation, distance));
	}

	public void update(Line line) {
		this.name = line.getName();
		this.color = line.getColor();
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

	public void addStation(Station upStation, Station downStation, int distance) {
		Section section = new Section(this, upStation, downStation, distance);
		sections.add(section);
		upStation.addLine(section);
		downStation.addLine(section);
	}

	public List<Station> getStations() {
		return sections.stream()
			.map(Section::getStations)
			.flatMap(Collection::stream)
			.distinct()
			.collect(Collectors.toList());
	}
}
