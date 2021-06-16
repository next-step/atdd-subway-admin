package nextstep.subway.section.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
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
		if(!isEmptySection()) {
			validateSection(newSection);
			mapUpStationEqualsUpStation(newSection);
			mapUpStationEqualsDownStation(newSection);
			mapDownStationEqualsDownStation(newSection);
		}
		this.sections.add(newSection);
	}

	private boolean isEmptySection() {
		return this.sections.size() == 0;
	}

	private void validateSection(Section newSection) {
		boolean isExistsUpStation = this.toStations().contains(newSection.getUpStation());
		boolean isExistsDownStation = this.toStations().contains(newSection.getDownStation());
		if (isExistsUpStation && isExistsDownStation) {
			throw new RuntimeException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
		}
		if (!isExistsUpStation && !isExistsDownStation) {
			throw new RuntimeException("상행역과 하행역에 아무것도 포함되지 않습니다.");
		}
	}

	private void mapUpStationEqualsUpStation(Section newSection) {
		this.sections.stream()
			.forEach(section -> {
				if(section.isUpStationEqualsUpStation(newSection)){
					section.setWhenUpStationEqualsUpStation(newSection);
				}
			});
	}

	private void mapUpStationEqualsDownStation(Section newSection) {
		this.sections.stream()
			.forEach(section -> {
				if(section.isUpStationEqualsDownStation(newSection)){
					section.setWhenUpStationEqualsDownStation(newSection);
				}
			});
	}

	private void mapDownStationEqualsDownStation(Section newSection) {
		this.sections.stream()
			.forEach(section -> {
				if(section.isDownStationEqualsDownStation(newSection)){
					section.setWhenDownStationEqualsDownStation(newSection);
				}
			});
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

}
