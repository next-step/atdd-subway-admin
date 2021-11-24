package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Line extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String name;

	private String color;

	@OneToMany(mappedBy = "line", fetch = FetchType.LAZY)
	private List<Section> sections = new ArrayList<>();

	protected Line() {
	}

	public Line(String name, String color) {
		this.name = name;
		this.color = color;
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

	public void addSection(Section section) {
		this.sections.add(section);
	}

	public int sectionsSize() {
		return this.sections.size();
	}

	public List<Station> getStations() {
		sections.sort(Comparator.comparingInt(Section::getSequence));
		List<Station> stations = sections.stream()
			.map(Section::getDownStation)
			.collect(Collectors.toList());
		stations.add(0, sections.stream()
			.findFirst()
			.get()
			.getUpStation());
		return stations;
	}
}
