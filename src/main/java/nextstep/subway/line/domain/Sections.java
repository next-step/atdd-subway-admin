package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import nextstep.subway.common.exception.IllegalDistanceException;

/**
 * @author : byungkyu
 * @date : 2021/01/06
 * @description :
 **/
@Embeddable
public class Sections {

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "line_id")
	private List<Section> sections = new ArrayList<>();

	protected Sections() {
	}

	public List<Section> getSections() {
		return sections;
	}

	public void addSection(Section section) {
		if (sections.isEmpty()) {
			sections.add(section);
			return;
		}

		validateSection(section);
		//역 사이에 새로운 역 등록
		if (isEqualToUpStation(section.getUpStationId())) {
			addSectionSameUpStation(section);
			return;
		}

		// 새로운 역을 상행 종점으로 등록할 경우
		if (isEqualToUpStation(section.getDownStationId())) {
			addSectionNewUpStation(section);
			return;
		}

		//새로운 역을 하향 종점으로 등록할 경우
		if (isEqualToDownStation(section.getUpStationId())) {
			addSectionNewDownStation(section);
		}
	}

	private void validateSection(Section section) {
		sections.forEach(originSection -> {
			if(originSection.isInValidDistance(section.getDistance())) throw new IllegalDistanceException();
		});
	}

	private void addSectionNewDownStation(Section section) {
		sections.stream()
			.filter(originSection -> originSection.getUpStationId().equals(section.getDownStationId()))
			.findFirst()
			.ifPresent(
				originSection -> originSection.changeDownStation(section.getUpStationId(), section.getDistance()));

		sections.add(section);
	}

	private void addSectionNewUpStation(Section section) {
		sections.stream()
			.filter(originSection -> originSection.getUpStationId().equals(section.getDownStationId()))
			.findFirst()
			.ifPresent(
				originSection -> originSection.changeUpStation(section.getDownStationId(), section.getDistance()));

		sections.add(section);
	}

	private void addSectionSameUpStation(Section section) {
		sections.stream()
			.filter(originSection -> originSection.getUpStationId().equals(section.getUpStationId())
				&& originSection.getDistance() > section
				.getDistance())
			.findFirst()
			.ifPresent(originSection -> originSection.changeUpStation(section.getDownStationId(),
				originSection.getDistance() - section.getDistance()));

		sections.add(section);
	}

	private boolean isEqualToDownStation(Long stationId) {
		return sections.stream()
			.anyMatch(originSection -> originSection.getDownStationId().equals(stationId));
	}

	private boolean isEqualToUpStation(Long stationId) {
		return sections.stream()
			.anyMatch(originSection -> originSection.getUpStationId().equals(stationId));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Sections sections1 = (Sections)o;
		return Objects.equals(getSections(), sections1.getSections());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getSections());
	}
}
