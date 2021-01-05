package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line extends BaseEntity {

	private final static int MIN_SECTION_COUNT = 2;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String name;

	private String color;

	@Embedded
	private Sections sections;

	public Line() {
	}

	public Line(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public Line(String name, String color, Station upStation, Station downStation, int distance) {
		this.name = name;
		this.color = color;
		Section upSection = new Section(this, null, downStation, 0, upStation);
		Section downSection = new Section(this, upStation, null, distance, downStation);
		this.sections = new Sections(upSection, downSection);
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

	public List<Section> getLineSections() {
		return sections.getSections();
	}

	public void addSection(Section section, Line line) {
		this.sections.addSection(section, line);
		section.addLine(this);
	}

	public boolean isImpossibleRemoveSection() {
		if(this.sections.getSections().size() == MIN_SECTION_COUNT){
			return true;
		}
		return false;
	}
}
