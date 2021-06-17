package nextstep.subway.section.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
	List<Section> sections = new LinkedList<>();

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
		if (isExistsUpStation && isExistsDownStation) {
			throw new RuntimeException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
		}
		if (!isExistsUpStation && !isExistsDownStation) {
			throw new RuntimeException("상행역과 하행역에 아무것도 포함되지 않습니다.");
		}
	}

	public void remove(Section section) {
		this.sections.remove(section);
	}

	public List<Station> toStations() {
		return this.sections.stream()
			.map(section -> section.toStations())
			.flatMap(station -> station.stream())
			.distinct()
			.collect(Collectors.toList());
	}

	private boolean isExistsUpStation(Section otherSection) {
		return this.sections.stream()
			.anyMatch(section -> section.isUpStationEqualsUpStation(otherSection));
	}

	private boolean isExistDownStation(Section otherSection) {
		return this.sections.stream()
			.anyMatch(section -> section.isDownStationEqualsDownStation(otherSection));
	}

	private void overrideIfExistsUpStation(final Section newSection) {
		sections.stream()
			.filter(section -> newSection.isUpStationEqualsDownStation(section))
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
