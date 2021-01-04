package nextstep.subway.section.domain;

import nextstep.subway.common.exception.AlreadyExistsStationException;
import nextstep.subway.common.exception.NotIncludeLineBothStationException;
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

	public void addSection(Section newSection) {
		//todo
		boolean isExistsUpStation = isExistsUpStationInSection(newSection.getUpStation());
		boolean isExistsDownStation = isExistsDownStationInSection(newSection.getDownStation());

		validateAddSection(isExistsUpStation,isExistsDownStation );

		if (isExistsUpStation) {
			this.sections.stream()
					.filter(oldSection -> oldSection.isDownStationInSection())
					.findFirst()
					.ifPresent(section -> section.updateDownStation(newSection.getDownStation(), newSection.getDistanceMeter()));

			this.sections.stream()
					.filter(oldSection -> oldSection.isUpStationInSection(newSection.getUpStation()))
					.findFirst()
					.ifPresent(section -> section.updateUpStation(newSection.getDownStation(), newSection.getDistanceMeter()));

		}
		if (isExistsDownStation) {
			this.sections.stream()
					.filter(oldSection -> oldSection.isUpStationInSection())
					.findFirst()
					.ifPresent(section -> section.updateUpStation(newSection.getUpStation(), newSection.getDistanceMeter()));

			this.sections.stream()
					.filter(oldSection -> oldSection.isDownStationInSection(newSection.getDownStation()))
					.findFirst()
					.ifPresent(section -> section.updateDownStation(newSection.getUpStation(), newSection.getDistanceMeter()));

		}

		this.sections.add(newSection);
	}

	private boolean isExistsDownStationInSection(Station downStation) {
		return this.sections.stream()
				.anyMatch(section -> section.isDownStationInSection(downStation));
	}

	private boolean isExistsUpStationInSection(Station upStation) {
		return this.sections.stream()
				.anyMatch(section -> section.isUpStationInSection(upStation));
	}

	private void validateAddSection(boolean isExistsUpStation, boolean isExistsDownStation) {
		if (isExistsUpStation && isExistsDownStation) {
			throw new AlreadyExistsStationException();
		}

		if (!isExistsUpStation && !isExistsDownStation) {
			throw new NotIncludeLineBothStationException();
		}

	}

}
