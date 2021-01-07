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
import nextstep.subway.common.BaseEntity;
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
	private Sections sections;

	public Line(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public Line(String name, String color, Station upStation, Station downStation, int distance) {
		this(name, color);
		this.sections = Sections.of(this, upStation, downStation, distance);
	}

	public Line(long id, String name, String color, Station upStation, Station downStation, int distance) {
		this(name, color);
		this.id = id;
		this.sections = Sections.of(this, upStation, downStation, distance);
	}

	public void update(Line line) {
		this.name = line.getName();
		this.color = line.getColor();
	}

	public void addSection(Station upStation, Station downStation, int distance) {
		this.sections.add(new Section(this, upStation, downStation, distance));
	}

	public List<Section> sections() {
		return this.sections.getSections();
	}

	public List<Station> stations() {
		return this.sections.getStations();
	}
}
