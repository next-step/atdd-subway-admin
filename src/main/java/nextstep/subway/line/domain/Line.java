package nextstep.subway.line.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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

	private Line(String name, String color, Station upStation, Station downStation, int distance) {
		this.name = name;
		this.color = color;
		this.initSection(Section.create(this, upStation, downStation, distance));
	}

	public static Line create(String name, String color, Station upStation, Station downStation, int distance) {
		return new Line(
			name,
			color,
			upStation,
			downStation,
			distance
		);
	}

	private void initSection(Section section) {
		this.sections.initSection(section);
	}

	public void addSection(Section section) {
		this.sections.add(section);
	}

	public void update(LineRequest line) {
		this.name = line.getName();
		this.color = line.getColor();
	}

	public List<Section> getAllSection() {
		return this.sections.getSections();
	}
}
