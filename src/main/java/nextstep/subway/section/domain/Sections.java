package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
	private List<Section> sections = new ArrayList<>();

	public Sections() {
	}

	public Sections(List<Section> sections) {
		this.sections = sections;
	}

	public void add(Section newSection) {
		boolean isDone = addBeside(newSection);
		if (isDone) {
			return;
		}

		isDone = addInside(newSection);
		if (isDone) {
			return;
		}

		throw new IllegalArgumentException("잘못된 구간정보입니다.");
	}

	public List<Station> stationsInOrder() {
		Section firstUpSection = findFirstUpSection();
		Map<Station, Section> stationMap = this.sections.stream()
			  .collect(Collectors.toMap(Section::getUpStation, Function.identity()));

		List<Station> stations = new ArrayList<>();
		stations.add(firstUpSection.getUpStation());
		stations.add(firstUpSection.getDownStation());

		Section currentSection = firstUpSection;
		while(stations.size() < this.sections.size() + 1) {
			currentSection = stationMap.get(currentSection.getDownStation());
			stations.add(currentSection.getDownStation());
		}

		return stations;
	}

	private Section findFirstUpSection() {
		Map<Station, Section> downStationMap = this.sections.stream()
			  .collect(Collectors.toMap(Section::getDownStation, Function.identity()));

		return this.sections.stream()
			  .filter(section -> !downStationMap.containsKey(section.getUpStation()))
			  .findFirst().orElseThrow(() -> new NoSuchElementException("상행종점역을 찾을 수 없습니다."));
	}

	private boolean addBeside(Section newSection) {
		if (this.sections.isEmpty()) {
			this.sections.add(newSection);
			return true;
		}

		Optional<Section> maybeSection = this.sections.stream()
			  .filter(section -> section.isLinked(newSection))
			  .filter(section -> !section.isSame(newSection))
			  .findFirst();
		if (!maybeSection.isPresent()) {
			return false;
		}

		this.sections.add(newSection);
		return true;
	}

	private boolean addInside(Section newSection) {
		List<Section> matchSections = this.sections.stream()
			  .filter(section -> section.isInside(newSection))
			  .collect(Collectors.toList());
		if (matchSections.isEmpty()) {
			return false;
		}

		if (matchSections.size() > 1) {
			throw new IllegalArgumentException("잘못된 구간정보입니다.");
		}

		Section section = matchSections.get(0);
		addInsideUpSection(section, newSection);
		addInsideDownSection(section, newSection);
		this.sections.add(newSection);

		return true;
	}

	private void addInsideDownSection(Section section, Section newSection) {
		if (!section.isDownStationEquals(newSection)) {
			return;
		}

		int remainDistance = calculateDistance(section, newSection);
		section.changeDownStation(newSection.getUpStation(), remainDistance);
	}

	private void addInsideUpSection(Section section, Section newSection) {
		if (!section.isUpStationEquals(newSection)) {
			return;
		}

		int remainDistance = calculateDistance(section, newSection);
		section.changeUpStation(newSection.getDownStation(), remainDistance);
	}

	private int calculateDistance(Section section, Section newSection) {
		int distance = section.calculateDistance(newSection);
		if (distance < 1) {
			throw new IllegalArgumentException("추가하려는 구간의 길이는 기존 구간의 길이보다 작아야 합니다.");
		}

		return distance;
	}

	public List<Section> getSections() {
		return sections;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Sections sections1 = (Sections) o;
		if (this.sections.size() != sections1.sections.size()) {
			return false;
		}

		return this.sections.containsAll(sections1.sections);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sections);
	}
}
