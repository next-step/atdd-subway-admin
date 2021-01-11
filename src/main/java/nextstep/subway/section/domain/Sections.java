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
		if (haveToUpdateExistsStationInSection(newSection.getDownStation(), newSection.getUpStation())) {
			boolean haveToUpdateUpStation = isExistsUpStationInSection(newSection.getUpStation(), newSection.getDownStation());
			updateExistsStationInSection(haveToUpdateUpStation, newSection);
			this.sections.add(newSection);
			return;
		}

		if (isNewUpStationInSection(newSection.getDownStation())) {
			addNewUpStation(newSection, line);
			return;
		}
		addNewDownStation(newSection, line);
	}

	private void updateExistsStationInSection(boolean haveToUpdateUpStation, Section newSection) {
		if (haveToUpdateUpStation) {
			addBetweenSectionExistsUpStation(newSection);
		}
		addBetweenSectionExistsDownStation(newSection);
	}

	private boolean haveToUpdateExistsStationInSection(Station downStation, Station upStation) {
		//하행, 상행  새로운 종점을 만들지 않아도 된다. 기존 역 업데이트 여부
		return !this.sections.stream()
				.anyMatch(section -> section.isUpStationInSection(downStation)) && !this.sections.stream()
				.anyMatch(section -> section.isDownStationInSection(upStation));
	}

	private boolean isExistsUpStationInSection(Station upStation, Station downStation) {
		boolean isExistsUpStation = this.sections.stream()
				.anyMatch(section -> section.isUpStationInSection(upStation));
		boolean isExistsDownStation = this.sections.stream()
				.anyMatch(section -> section.isDownStationInSection(downStation));

		validateExistsStation(isExistsUpStation, isExistsDownStation);

		return isExistsUpStation;
	}

	private void addBetweenSectionExistsDownStation(Section newSection) {
		this.sections.stream()
				.filter(oldSection -> oldSection.isUpStationInSection())
				.findFirst()
				.ifPresent(section -> section.updateUpStationWhenAddSection(newSection.getUpStation(), newSection.getDistanceMeter()));

		this.sections.stream()
				.filter(oldSection -> oldSection.isDownStationInSection(newSection.getDownStation()))
				.findFirst()
				.ifPresent(section -> section.updateDownStation(newSection.getUpStation(), newSection.getDistanceMeter()));
	}

	private void addBetweenSectionExistsUpStation(Section newSection) {
		this.sections.stream()
				.filter(oldSection -> oldSection.isDownStationInSection())
				.findFirst()
				.ifPresent(section -> section.updateDownStation(newSection.getDownStation(), newSection.getDistanceMeter()));

		this.sections.stream()
				.filter(oldSection -> oldSection.isUpStationInSection(newSection.getUpStation()))
				.findFirst()
				.ifPresent(section -> section.updateUpStationWhenAddSection(newSection.getDownStation(), newSection.getDistanceMeter()));
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
				.ifPresent(section -> section.updateUpStationWhenAddSection(newSection.getUpStation(), newSection.getDistanceMeter()));

		this.sections.stream()
				.filter(oldSection -> oldSection.isUpStationInSection())
				.findFirst()
				.ifPresent(section -> section.plusDistance(newSection.getDistanceMeter()));

		Section upSection = new Section(newSection.getDownStation(), line, newSection.getUpStation());
		this.sections.add(upSection);
	}

	private boolean isNewUpStationInSection(Station downStation) {
		return this.sections.stream()
				.anyMatch(section -> section.isUpStationInSection(downStation));
	}


	private void validateExistsStation(boolean isExistsUpStation, boolean isExistsDownStation) {
		if (isExistsUpStation && isExistsDownStation) {
			throw new AlreadyExistsStationException();
		}

		if (!isExistsUpStation && !isExistsDownStation) {
			throw new NotIncludeLineBothStationException();
		}

	}

}
