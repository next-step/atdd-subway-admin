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

	@OneToMany(mappedBy = "line", cascade = CascadeType.REMOVE)
	private List<Section> sections = new ArrayList<>();

	protected Line() {
	}

	private Line(Long id, String name, String color) {
		this.id = id;
		this.name = name;
		this.color = color;
	}

	public static Line of(Long id, String name, String color) {
		return new Line(id, name, color);
	}

	public static Line of(Long id, String name, String color, List<Section> sections) {
		Line line = new Line(id, name, color);
		line.sections = sections;
		return line;
	}

	public void update(Line line) {
		this.name = line.getName();
		this.color = line.getColor();
	}

	public void addSection(Section section) {
		this.sections.add(section);
		section.setLine(this);
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

	public List<Station> getStations() {
		return sections.stream()
			.map(Section::getStations)
			.flatMap(Collection::stream)
			.collect(Collectors.toList());
	}

	public List<Section> getSections() {
		return this.sections;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Line line = (Line)o;

		return id.equals(line.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

}
