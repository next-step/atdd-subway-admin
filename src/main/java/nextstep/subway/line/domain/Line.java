package nextstep.subway.line.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang3.StringUtils;

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

	@Embedded
	private Sections sections = Sections.of();

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
		line.sections = Sections.of(sections);
		return line;
	}

	public static Line of(Long id, String name, String color, Long upStationId, Long downStationId, int distance) {
		Station upStation = Station.of(upStationId);
		Station downStation = Station.of(downStationId);
		Section section = Section.of(null, upStation, downStation, distance);
		Line line = new Line(id, name, color);
		line.addSection(section);
		return line;
	}

	public void update(Line line) {
		if (StringUtils.isNotBlank(line.getName())) {
			this.name = line.getName();
		}
		if (StringUtils.isNotBlank(line.getColor())) {
			this.color = line.getColor();
		}
	}

	public void addSection(Section section) {
		this.sections.add(section);
		section.setLine(this);
	}

	public void updateSections(Section section) {
		this.sections.update(section);
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

	public List<Station> getOrderedStations() {
		return sections.getOrderedStations();
	}

	public Sections getSections() {
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
