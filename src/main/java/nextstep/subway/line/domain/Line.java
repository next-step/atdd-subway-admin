package nextstep.subway.line.domain;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.exception.NotFoundException;
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
	private Sections sections = new Sections();

	protected Line() {
	}

	public Line(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public Line(String name, String color, Station upStation, Station downStation, int distance) {
		this.name = name;
		this.color = color;
		sections.addSection(new Section(upStation.getId(), downStation.getId(), distance));
	}

	public void update(Line line) {
		validate(line);
		this.name = line.getName();
		this.color = line.getColor();
	}

	private void validate(Line line) {
		if (line == null)
			throw new NotFoundException();
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

	public List<Long> getStationsIds() {
		return sections.getSections().stream()
			.map(section -> section.getStationsIds())
			.flatMap(Collection::stream)
			.sorted(Comparator.comparingLong(Long::longValue))
			.collect(Collectors.toList());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Line line = (Line)o;
		return Objects.equals(getId(), line.getId()) && Objects.equals(getName(), line.getName())
			&& Objects.equals(getColor(), line.getColor()) && Objects.equals(sections, line.sections);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getName(), getColor(), sections);
	}

	public void addSection(Section section) {
		sections.addSection(section);
	}
}
