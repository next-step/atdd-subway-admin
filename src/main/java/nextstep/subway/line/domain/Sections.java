package nextstep.subway.line.domain;

import java.util.ArrayList;
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
	public static final int START_SECTION_INDEX = 0;

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
		if (ifAddSectionsStartLocation(section)) {
			addSectionsStartLocation(section);
			return;
		}
		if (ifAddSectionsEndLocation(section)) {
			addSectionsEndLocation(section);
			return;
		}

	}

	private void addSectionsEndLocation(Section section) {
		int endSectionIndex = sections.size() - 1;
		Section endSection = sections.get(endSectionIndex);
		section.updateSequence(endSection.getSequence()+1);
		sections.add(endSectionIndex, section);
	}

	private boolean ifAddSectionsEndLocation(Section section) {
		return sections.stream()
			.filter(s -> s.getSequence() == sections.size())
			.filter(s -> s.getDownStation().equals(section.getUpStation()))
			.findFirst()
			.isPresent();
	}

	private boolean ifAddSectionsStartLocation(Section section) {
		return sections.stream()
			.filter(s -> s.getSequence() == 1)
			.filter(s -> s.getUpStation().equals(section.getDownStation()))
			.findFirst()
			.isPresent();
	}

	private void addSectionsStartLocation(Section section) {
		Section startSection = sections.get(START_SECTION_INDEX);
		section.updateSequence(startSection.getSequence());
		startSection.updateSequence(section.getSequence() + 1);
		sections.add(START_SECTION_INDEX, section);
	}

}
