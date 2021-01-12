package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.exception.MinSectionDeleteException;
import nextstep.subway.common.exception.NotFoundSectionException;
import nextstep.subway.common.exception.NotIncludeStationInSection;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
		return this.sections.getSections().size() == MIN_SECTION_COUNT;
	}

	public void removeTerminal(Section removedSection) {
		boolean isUpTerminal = removedSection.isUpTerminal();
		Section updatedSection = getUpdateSection(removedSection, isUpTerminal);
		updatedSection.updateToTerminal(isUpTerminal);
		this.getLineSections().remove(removedSection);
	}

	private Section getUpdateSection(Section removedSection, boolean isUpTerminal) {
		Station linkedStation =	removedSection.getUpdateSection(isUpTerminal);
		return this.getLineSections().stream()
		.filter(section -> section.getMainStation().getId() == linkedStation.getId())
		.findAny()
		.orElseThrow(() -> new NotFoundSectionException(linkedStation.getId()));
	}

	public void removeBetweenSection(Section removedSection) {
		Section upSection = this.getLineSections().stream()
				.filter(section -> section.getMainStation().getId() == removedSection.getUpStation().getId())
				.findAny()
				.orElseThrow(() -> new NotFoundSectionException(removedSection.getUpStation().getId()));
		Section downSection = this.getLineSections().stream()
				.filter(section -> section.getMainStation().getId() == removedSection.getDownStation().getId())
				.findAny()
				.orElseThrow(() -> new NotFoundSectionException(removedSection.getDownStation().getId()));

		upSection.updateDownStation(removedSection.getDownStation());
		downSection.updateUpStationWhenDeleteSection(removedSection.getUpStation(), removedSection.getDistanceMeter());
		this.getLineSections().remove(removedSection);
	}

	public void removeSectionByStationId(Long stationId) {
		Optional<Section> sectionOptional = this.getLineSections().stream().filter
				(sec -> (sec.getUpStation() == null && sec.getMainStation().getId() == stationId) ||
				(sec.getDownStation() == null && sec.getMainStation().getId() == stationId) || (sec.getMainStation().getId() == stationId))
				.findAny();

		validate(sectionOptional);

		if (sectionOptional.get().isTerminal()) {
			this.removeTerminal(sectionOptional.get());
			return;
		}
		//중간역이 삭제되는 경우
		this.removeBetweenSection(sectionOptional.get());
	}

	private void validate(Optional<Section> section) {
		if (!section.isPresent()) {
			throw new NotIncludeStationInSection();
		}

		if (this.isImpossibleRemoveSection()) {
			throw new MinSectionDeleteException();
		}
	}
}
