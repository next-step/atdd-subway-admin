package nextstep.subway.line.domain;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

@Entity
public class Line extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String name;
	private String color;

	@OneToMany(mappedBy = "line")
	private List<Section> sections = new LinkedList<>();

	public Line() {
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
		return this.id;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	public void addSection(Section section) {
		this.sections.add(section);
		section.setLine(this);
	}

	public List<Section> getSections() {
		return this.sections;
	}

	public List<Station> getStations() {
		List<Station> stations = new LinkedList<>();
		this.sections.stream().forEach(section -> {
			stations.add(section.getUpStation());
			stations.add(section.getDownStation());
		});
		return stations;
	}
}
