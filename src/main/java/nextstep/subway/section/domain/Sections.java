package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.section.exception.InvalidSectionException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
	List<Section> sections = new ArrayList<>();

	public Sections() {

	}

	public void add(Section newSection) {
		if (!isEmptySection()) {
			validateSection(newSection);
			overrideIfExistsUpStation(newSection);
			overrideIfExistsDownStation(newSection);
		}
		this.sections.add(newSection);
	}

	private boolean isEmptySection() {
		return this.sections.size() == 0;
	}

	private void validateSection(Section newSection) {
		boolean isExistsUpStation = isExistsUpStation(newSection);
		boolean isExistsDownStation = isExistDownStation(newSection);
		if (isExistsSection(newSection)) {
			throw new InvalidSectionException("이미 노선에 상행역과 하행역 구간이 모두 등록되어 있습니다.");
		}
		if (!isExistsUpStation && !isExistsDownStation) {
			throw new InvalidSectionException("상행역과 하행역에 아무것도 포함되지 않습니다.");
		}
	}

	public void remove(Section section) {
		this.sections.remove(section);
	}

	public List<Station> toStations() {
		return sortStation().stream()
			.map(section -> section.toStations())
			.flatMap(station -> station.stream())
			.distinct()
			.collect(Collectors.toList());
	}

	private List<Section> sortStation() {
		List sortedSections = new LinkedList();
		Section currentSection = getFirstSection();
		sortedSections.add(currentSection);
		while (getNextSection(currentSection).isPresent()) {
			currentSection = getNextSection(currentSection).get();
			sortedSections.add(currentSection);
		}
		return sortedSections;
	}

	private Optional<Section> getNextSection(Section currentSection) {
		return this.sections.stream()
			.filter(section -> currentSection.isDownStationEqualsUpStation(section))
			.findFirst();
	}

	private Section getFirstSection() {
		return this.sections.stream()
			.filter(section -> isFirstSection(section))
			.findFirst()
			.orElse(sections.get(0));
	}

	private boolean isFirstSection(Section section) {
		return this.sections.stream()
			.noneMatch(otherSection -> section.isUpStationEqualsDownStation(otherSection));
	}

	private boolean isExistsSection(Section otherSection) {
		return this.sections.stream()
			.anyMatch(
				section -> section.isUpStationEqualsUpStation(otherSection) && section.isUpStationEqualsDownStation(
					otherSection));
	}

	private boolean isExistsUpStation(Section otherSection) {
		return this.sections.stream()
			.anyMatch(
				section -> section.isUpStationEqualsUpStation(otherSection) || section.isUpStationEqualsDownStation(
					otherSection));
	}

	private boolean isExistDownStation(Section otherSection) {
		return this.sections.stream()
			.anyMatch(
				section -> section.isDownStationEqualsDownStation(otherSection) || section.isDownStationEqualsUpStation(
					otherSection));
	}

	private void overrideIfExistsUpStation(final Section newSection) {
		sections.stream()
			.filter(section -> newSection.isUpStationEqualsUpStation(section))
			.findFirst()
			.ifPresent(section -> section.overrideUpStation(newSection));
	}

	private void overrideIfExistsDownStation(final Section newSection) {
		sections.stream()
			.filter(section -> newSection.isDownStationEqualsDownStation(section))
			.findFirst()
			.ifPresent(section -> section.overrideDownStation(newSection));
	}

}
