package nextstep.subway.line.domain;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.common.domain.Color;
import nextstep.subway.common.domain.Name;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionGroup;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationGroup;

@Entity
public class Line extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	private Name name;

	@Embedded
	private Color color;

	@Embedded
	private SectionGroup sectionGroup;

	protected Line() {
	}

	public Line(String name, String color) {
		this.name = Name.generate(name);
		this.color = Color.generate(color);
	}

	public Line(String name, String color, Station upStation, Station downStation, String distance) {
		this.name = Name.generate(name);
		this.color = Color.generate(color);
		Section section = Section.generate(this, upStation, downStation, distance);
		this.sectionGroup = new SectionGroup(section);
	}

	public void update(Line line) {
		this.name = Name.generate(line.name());
		this.color = Color.generate(line.color());
	}

	public Long id() {
		return id;
	}

	public String name() {
		return name.value();
	}

	public String color() {
		return color.value();
	}

	public SectionGroup sectionGroup() {
		return sectionGroup;
	}

	public StationGroup stationGroup() {
		return sectionGroup.sections().stream()
			.flatMap(section -> Arrays.stream(new Station[] {section.upStation(), section.downStation()}))
			.distinct()
			.collect(Collectors.collectingAndThen(Collectors.toList(), StationGroup::new));
	}

	public void addSection(Section section) {
		sectionGroup.add(section);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		Line line = (Line)object;
		return Objects.equals(id, line.id) &&
			Objects.equals(name, line.name) &&
			Objects.equals(color, line.color) &&
			Objects.equals(sectionGroup, line.sectionGroup);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, color, sectionGroup);
	}
}
