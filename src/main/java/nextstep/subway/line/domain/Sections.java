package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.common.exception.DuplicateSectionException;
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

	public Sections(List<Section> sections) {
		this.sections = sections;
	}

	public List<Station> getStations() {
		return sections.stream()
			.map(Section::getStations)
			.flatMap(Collection::stream)
			.distinct()
			.collect(Collectors.toList());
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

	public void add(Section section) {
		sections.add(section);
	}

	public void addStation(Line line, Station upStation, Station downStation, int distance) {
		List<Station> stations = getStations();

		boolean upStationMatched = stations.stream().anyMatch(station -> station == upStation);
		boolean downStationMatched = stations.stream().anyMatch(station -> station == downStation);

		if(upStationMatched && downStationMatched) {
			throw new DuplicateSectionException();
		}

		if(!upStationMatched && !downStationMatched) {
			throw new NotMatchStationException();
		}

		// 추가 상행역이 역들 중에 있는 경우
		if(upStationMatched) {
			// 역들 사이에 들어가는 경우, 기존 구간 정보 업데이트
			findSectionByUpStation(upStation)
				.ifPresent(section -> section.updateUpStation(downStation, distance));
		}

		// 추가 하행역이 역들 중에 있는 경우
		if(downStationMatched) {
			//역들 사이에 들어가는 경우, 기존 구간 정보 업데이트
			findSectionByDownStation(downStation)
				.ifPresent(section -> section.updateDownStation(upStation, distance));
		}

		sections.add(new Section(line, upStation, downStation, distance));
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
