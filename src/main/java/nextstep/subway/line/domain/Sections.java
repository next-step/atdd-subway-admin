package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
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
		if (ifAddStartLocation(section)) {
			addSectionStartLocation(section);
			return;
		}
		if (ifAddEndLocation(section)) {
			addSectionEndLocation(section);
			return;
		}
		addSectionMiddleLocation(section);
	}

	private void addSectionMiddleLocation(Section section) {
		if (ifAddMiddleStartLocation(section)) {
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
		updateNonMiddleLocationSectionSequence(middleSection);
		section.updateSequence(middleSection.getSequence() + 1);
		middleSection.updateDownStation(section.getUpStation());
		middleSection.updateDistance(middleSection.getDistance() - section.getDistance());
		sections.add(section.getSequence() - 1, section);
	}

	private void addSectionMiddleStartLocation(Section section) {
		Section middleSection = sections.stream()
			.filter(s -> s.getUpStation().equals(section.getUpStation()))
			.filter(s -> s.getDistance() > section.getDistance())
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("신규구간의 길이는 기존의 구간보다 클 수 없습니다."));
		updateNonMiddleLocationSectionSequence(middleSection);
		section.updateSequence(middleSection.getSequence());
		middleSection.updateSequence(middleSection.getSequence() + 1);
		middleSection.updateUpStation(section.getDownStation());
		middleSection.updateDistance(middleSection.getDistance() - section.getDistance());
		sections.add(section.getSequence() - 1, section);
	}

	private void updateNonMiddleLocationSectionSequence(Section middleSection) {
		sections.stream()
			.filter(s -> s.getSequence() > middleSection.getSequence())
			.forEach(s -> s.updateSequence(s.getSequence() + 1));
	}

	private boolean ifAddMiddleStartLocation(Section section) {
		return sections.stream()
			.filter(s -> s.getUpStation().equals(section.getUpStation()))
			.findFirst()
			.isPresent();
	}

	private void addSectionEndLocation(Section section) {
		Section endSection = sections.get(sections.size() - 1);
		section.updateSequence(endSection.getSequence() + 1);
		sections.add(sections.size() - 1, section);
	}

	private boolean ifAddEndLocation(Section section) {
		return sections.stream()
			.filter(s -> s.getSequence() == sections.size())
			.filter(s -> s.getDownStation().equals(section.getUpStation()))
			.findFirst()
			.isPresent();
	}

	private boolean ifAddStartLocation(Section section) {
		return sections.stream()
			.filter(s -> s.getSequence() == 1)
			.filter(s -> s.getUpStation().equals(section.getDownStation()))
			.findFirst()
			.isPresent();
	}

	private void addSectionStartLocation(Section section) {
		Section startSection = sections.get(0);
		section.updateSequence(startSection.getSequence());
		startSection.updateSequence(section.getSequence() + 1);
		sections.add(0, section);
	}

	public boolean allContain(Station... stations) {
		return getAllStationsBySections().containsAll(Arrays.asList(stations));
	}

	public boolean notContain(Station[] stations) {
		return Arrays.asList(stations).stream()
			.filter(getAllStationsBySections()::contains)
			.count() == 0;
	}
}
