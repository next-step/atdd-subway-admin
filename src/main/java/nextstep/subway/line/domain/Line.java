package nextstep.subway.line.domain;

import java.util.List;

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
	private Sections sections;

	protected Line() {
	}

	public Line(String name, String color) {
		this.name = name;
		this.color = color;
		this.sections = new Sections();
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

	public void initSection(Section section) {
		sections.init(section);
		section.initLine(this);
		section.initSequence(this);
	}

	public int sectionsSize() {
		return sections.size();
	}

	public List<Station> getStations() {
		return sections.getAllStationsBySections();
	}

	public void addSection(Section section) {
		validateAllContainStation(section);
		validateNotContainStation(section);
		sections.add(section);
		section.initLine(this);
	}

	private void validateAllContainStation(Section section) {
		if (sections.allContain(section)) {
			throw new IllegalArgumentException("노선에 이미 전부 존재하는 역들입니다.");
		}
	}

	private void validateNotContainStation(Section section) {
		if (sections.notContain(section)) {
			throw new IllegalArgumentException("노선의 구간 내 일치하는 역이 없습니다.");
		}
	}
}
