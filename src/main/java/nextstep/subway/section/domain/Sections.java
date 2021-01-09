package nextstep.subway.section.domain;

import nextstep.subway.common.exception.AlreadyExistsStationException;
import nextstep.subway.common.exception.NotIncludeLineBothStationException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	public Sections() {
	}

	public Sections(Section upSection, Section downSection) {
		this.sections = Arrays.asList(upSection, downSection);
	}

	public List<Section> getSections() {
		return sections;
	}

	public void addSection(Section newSection, Line line) {
		boolean isExistsUpStation = isExistsUpStationInSection(newSection.getUpStation());
		boolean isExistsDownStation = isExistsDownStationInSection(newSection.getDownStation());

		boolean isNewUpStation = isNewUpStationInSection(newSection.getDownStation());
		boolean isNewDownStation = isNewDownStationInSection(newSection.getUpStation());

		validateAddSection(isExistsUpStation, isExistsDownStation, isNewUpStation, isNewDownStation);

		if (isNewUpStation) {
			addNewUpStation(newSection, line);
			return;
		}

		if (isNewDownStation) {
			addNewDownStation(newSection, line);
			return;
		}

		if (isExistsUpStation) {
			addBetweenSectionExistsUpStation(newSection, line);
		}
		if (isExistsDownStation) {
			addBetweenSectionExistsDownStation(newSection, line);
		}

		this.sections.add(newSection);
	}

	private void addBetweenSectionExistsDownStation(Section newSection, Line line) {
		this.sections.stream()
				.filter(oldSection -> oldSection.isUpStationInSection())
				.findFirst()
				.ifPresent(section -> section.updateUpStation(newSection.getUpStation(), newSection.getDistanceMeter()));

		this.sections.stream()
				.filter(oldSection -> oldSection.isDownStationInSection(newSection.getDownStation()))
				.findFirst()
				.ifPresent(section -> section.updateDownStation(newSection.getUpStation(), newSection.getDistanceMeter()));
	}

	private void addBetweenSectionExistsUpStation(Section newSection, Line line) {
		this.sections.stream()
				.filter(oldSection -> oldSection.isDownStationInSection())
				.findFirst()
				.ifPresent(section -> section.updateDownStation(newSection.getDownStation(), newSection.getDistanceMeter()));

		this.sections.stream()
				.filter(oldSection -> oldSection.isUpStationInSection(newSection.getUpStation()))
				.findFirst()
				.ifPresent(section -> section.updateUpStation(newSection.getDownStation(), newSection.getDistanceMeter()));
	}

	private void addNewDownStation(Section newSection, Line line) {
		this.sections.stream()
				.filter(oldSection -> oldSection.isUpStationInSection())
				.findFirst()
				.ifPresent(section -> section.updateDownStation(newSection.getDownStation()));

		Section downSection = new Section(newSection.getUpStation(), newSection.getDistanceMeter(), line, newSection.getDownStation());
		this.sections.add(downSection);
	}

	private void addNewUpStation(Section newSection, Line line) {
		this.sections.stream()
				.filter(oldSection -> oldSection.isDownStationInSection())
				.findFirst()
				.ifPresent(section -> section.updateUpStation(newSection.getUpStation(), newSection.getDistanceMeter()));

		this.sections.stream()
				.filter(oldSection -> oldSection.isUpStationInSection())
				.findFirst()
				.ifPresent(section -> section.plusDistance(newSection.getDistanceMeter()));

		Section upSection = new Section(newSection.getDownStation(), line, newSection.getUpStation());
		this.sections.add(upSection);
	}

	private boolean isNewDownStationInSection(Station upStation) {
		return this.sections.stream()
				.anyMatch(section -> section.isDownStationInSection(upStation));

	}

	private boolean isNewUpStationInSection(Station downStation) {
		return this.sections.stream()
				.anyMatch(section -> section.isUpStationInSection(downStation));
	}

	private boolean isExistsDownStationInSection(Station downStation) {
		return this.sections.stream()
				.anyMatch(section -> section.isDownStationInSection(downStation));
	}

	private boolean isExistsUpStationInSection(Station upStation) {
		return this.sections.stream()
				.anyMatch(section -> section.isUpStationInSection(upStation));
	}

	private void validateAddSection(boolean isExistsUpStation, boolean isExistsDownStation, boolean isNewUpStation, boolean isNewDownStation) {
		if (isNewUpStation || isNewDownStation) {
			return;
		}
		if (isExistsUpStation && isExistsDownStation) {
			throw new AlreadyExistsStationException();
		}

		if (!isExistsUpStation && !isExistsDownStation) {
			throw new NotIncludeLineBothStationException();
		}

	}

}
