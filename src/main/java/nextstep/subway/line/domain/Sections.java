package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.common.exception.NotMatchStationException;
import nextstep.subway.common.exception.SectionsRemoveInValidSizeException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

	public static final int DELETE_MINIMAL_SIZE = 1;

	@OneToMany(mappedBy = "line", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	public Sections() {
	}

	public List<Station> getStations() {
		return sections.stream()
			.map(Section::getStations)
			.flatMap(Collection::stream)
			.distinct()
			.collect(Collectors.toList());
	}

	public void add(Section section) {
		sections.add(section);
	}

	public Optional<Section> findSectionByDownStation(Station station) {
		return sections.stream()
			.filter(it -> it.getDownStation() == station)
			.findFirst();
	}

	public Optional<Section> findSectionByUpStation(Station station) {
		return sections.stream()
			.filter(it -> it.getUpStation() == station)
			.findFirst();
	}

	public void removeStation(Line line, Station station) {
		if(!isPresentStation(station)) {
			throw new NotMatchStationException();
		}

		if(!validateDelete()) {
			throw new SectionsRemoveInValidSizeException();
		}

		Optional<Section> upSection = findSectionByUpStation(station);
		Optional<Section> downSection = findSectionByDownStation(station);

		// 가운데를 지우는 경우
		if(upSection.isPresent() && downSection.isPresent()) {
			Station newUpStation = downSection.get().getUpStation();
			Station newDownStation = upSection.get().getDownStation();
			int newDistance = upSection.get().getDistance() + downSection.get().getDistance();
			sections.add(new Section(line, newUpStation, newDownStation, newDistance));
		}

		upSection.ifPresent(section -> sections.remove(section));
		downSection.ifPresent(section -> sections.remove(section));
	}

	private boolean validateDelete() {
		return sections.size() > DELETE_MINIMAL_SIZE;
	}

	private boolean isPresentStation(Station station) {
		return getStations().stream().anyMatch(st -> st == station);
	}
}
