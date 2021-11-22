package nextstep.subway.line.domain;

import java.util.List;
import java.util.Objects;

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

	@Embedded
	private Sections sections = new Sections();

	protected Line() {
	}

	public Line(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public void addSection(Section section) {
		sections.addSection(section);
		section.assignLine(this);
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

	public Sections getSections() {
		return sections;
	}

	public List<Section> getOrderedSections(){
		return sections.getOrderSections();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Line)) {
			return false;
		}

		Line line = (Line)o;
		return Objects.equals(getId(), line.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}

	@Override
	public String toString() {
		return "Line{" +
			"id=" + id +
			", name='" + name + '\'' +
			", color='" + color + '\'' +
			'}';
	}

	public void deleteStation(Station station) {
		sections.deleteStation(station);
	}
}
