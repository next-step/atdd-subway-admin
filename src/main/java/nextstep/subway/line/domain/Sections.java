package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<Section> sections = new ArrayList<>();

	public void init(Section section) {
		sections.add(section);
	}

	public int size() {
		return sections.size();
	}

	public List<Station> getAllStationsBySections() {
		sections.sort(Comparator.comparingInt(Section::getSequence));
		List<Station> stations = sections.stream()
			.map(Section::getDownStation)
			.collect(Collectors.toList());
		stations.add(0, sections.stream()
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("구간이 존재하지 않습니다."))
			.getUpStation());
		return stations;
	}

	public void add(Section section) {
		validateStationsContainAllOrNot(section);
		if (ifStartLocation(section.getDownStation())) {
			addSectionStartLocation(section);
			return;
		}
		if (ifEndLocation(section.getUpStation())) {
			addSectionEndLocation(section);
			return;
		}
		addSectionMiddleLocation(section);
	}

	private void validateStationsContainAllOrNot(Section section) {
		if (allContain(section)) {
			throw new IllegalArgumentException("노선에 이미 전부 존재하는 역들입니다.");
		}

		if (notContain(section)) {
			throw new IllegalArgumentException("노선의 구간 내 일치하는 역이 없습니다.");
		}
	}

	private boolean allContain(Section section) {
		return getAllStationsBySections()
			.containsAll(Arrays.asList(section.getUpStation(), section.getDownStation()));
	}

	private boolean notContain(Section section) {
		return getAllStationsBySections().stream()
			.noneMatch(s -> s.equals(section.getUpStation()) || s.equals(section.getDownStation()));
	}

	private void addSectionMiddleLocation(Section section) {
		if (ifMiddleStartLocation(section.getUpStation())) {
			addSectionMiddleStartLocation(section);
			return;
		}
		addSectionMiddleEndLocation(section);
	}

	private void addSectionMiddleEndLocation(Section section) {
		Section middleSection = sections.stream()
			.filter(s -> s.getDownStation().equals(section.getDownStation()))
			.filter(s -> s.getDistance() > section.getDistance())
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("신규구간의 길이는 기존의 구간보다 클 수 없습니다."));
		middleSection.updateDownStation(section.getUpStation());
		middleSection.updateDistance(middleSection.getDistance() - section.getDistance());
		sections.add(sections.indexOf(middleSection) + 1, section);
		updateSectionsSequence();
	}

	private void addSectionMiddleStartLocation(Section section) {
		Section middleSection = sections.stream()
			.filter(s -> s.getUpStation().equals(section.getUpStation()))
			.filter(s -> s.getDistance() > section.getDistance())
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("신규구간의 길이는 기존의 구간보다 클 수 없습니다."));
		middleSection.updateUpStation(section.getDownStation());
		middleSection.updateDistance(middleSection.getDistance() - section.getDistance());
		sections.add(sections.indexOf(middleSection), section);
		updateSectionsSequence();
	}

	private void updateSectionsSequence() {
		IntStream.range(0, sections.size())
			.forEach(index -> sections.get(index).updateSequence(index + 1));
	}

	private boolean ifMiddleStartLocation(Station station) {
		return sections.stream()
			.anyMatch(s -> s.getUpStation().equals(station));
	}

	private boolean ifEndLocation(Station station) {
		return sections.stream()
			.filter(s -> s.getSequence() == sections.size())
			.anyMatch(s -> s.getDownStation().equals(station));
	}

	private boolean ifStartLocation(Station station) {
		return sections.stream()
			.filter(s -> s.getSequence() == 1)
			.anyMatch(s -> s.getUpStation().equals(station));
	}

	private void addSectionEndLocation(Section section) {
		sections.add(section);
		updateSectionsSequence();
	}

	private void addSectionStartLocation(Section section) {
		sections.add(0, section);
		updateSectionsSequence();
	}


	public void deleteStation(Station station) {
		validateSectionsSize();
		validateStationNotContain(station);
		if (ifStartLocation(station)) {
			deleteSectionContainingStartLocation();
		}

		if (ifEndLocation(station)) {
			deleteSectionContainingEndLocation();
		}
		deleteSectionContainingMiddleLocation(station);
	}

	private void validateStationNotContain(Station station) {
		if (!getAllStationsBySections().contains(station)) {
			throw new IllegalArgumentException("노선 내 역이 존재하지 않습니다.");
		}
	}

	private void validateSectionsSize() {
		if (sections.size() == 1) {
			throw new IllegalArgumentException("노선 내 남은 지하철역이 2개 이상이여야 삭제 가능합니다.");
		}
	}

	private void deleteSectionContainingMiddleLocation(Station station) {
		Section deleteSection = this.sections.stream()
			.filter(s -> s.getUpStation().equals(station) || s.getDownStation().equals(station))
			.reduce((now, next) -> {
				now.updateDownStation(next.getDownStation());
				now.updateDistance(now.getDistance() + next.getDistance());
				return next;})
			.orElseThrow(() -> new IllegalArgumentException("구간 내 역이 존재하지 않습니다."));
		sections.remove(deleteSection);
		updateSectionsSequence();
	}

	private void deleteSectionContainingEndLocation() {
		sections.remove(sections.size() - 1);
		updateSectionsSequence();
	}

	private void deleteSectionContainingStartLocation() {
		sections.remove(0);
		updateSectionsSequence();
	}

}
