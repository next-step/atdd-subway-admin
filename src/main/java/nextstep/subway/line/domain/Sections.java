package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import nextstep.subway.common.exception.DuplicateAllStationException;
import nextstep.subway.common.exception.IllegalDistanceException;
import nextstep.subway.common.exception.NotExistAllStationException;
import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.common.exception.OneSectionCannotRemoveException;

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
		validateSectionAllStation(section);
		validateSectionDistance(section);
		validateNotExistAllStation(section);
	}

	private void validateNotExistAllStation(Section section) {
		sections.forEach(originSection -> {
			if (originSection.isNotExistAllStation(section.getUpStationId(), section.getDownStationId()))
				throw new NotExistAllStationException();
		});

	}

	private void validateSectionAllStation(Section section) {
		sections.forEach(originSection -> {
			if (originSection.isDuplicateAllStation(section.getUpStationId(), section.getDownStationId()))
				throw new DuplicateAllStationException();
		});
	}

	private void validateSectionDistance(Section section) {
		sections.forEach(originSection -> {
			if (originSection.isInValidDistance(section.getDistance()))
				throw new IllegalDistanceException();
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

	public void removeSectionByStationId(Long stationId) {
		if (sections.size() == 1) {
			throw new OneSectionCannotRemoveException();
		}

		Section frontStationSection = getSectionContainByStationId(filterEqualDownStationIdPredicate(stationId));
		Section backStationSection = getSectionContainByStationId(filterEqualUpStationIdPredicate(stationId));

		if (frontStationSection == null && backStationSection == null)
			throw new NotFoundException();

		sections.removeAll(Arrays.asList(frontStationSection, backStationSection));

		if (frontStationSection != null && backStationSection != null) {
			sections.add(new Section(frontStationSection.getUpStationId(), backStationSection.getDownStationId(),
				frontStationSection.getDistance() + backStationSection.getDistance()));
		}
	}

	private Section getSectionContainByStationId(Predicate<Section> filterEqualStationIdPredicate) {
		return sections.stream()
			.filter(filterEqualStationIdPredicate)
			.findFirst()
			.orElse(null);
	}

	private Predicate<Section> filterEqualDownStationIdPredicate(Long stationId) {
		return section -> section.getDownStationId().equals(stationId);
	}

	private Predicate<Section> filterEqualUpStationIdPredicate(Long stationId) {
		return section -> section.getUpStationId().equals(stationId);
	}

	protected Sections() {
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
