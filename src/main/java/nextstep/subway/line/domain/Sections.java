package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line")
	private List<Section> sections = new ArrayList<>();

	protected Sections() {
	}

	public void add(Section section) {
		sections.add(section);
	}

	public List<Station> getStations() {

		List<Section> orderedSections = getOrderedSections();
		return converToStations(orderedSections);
	}

	private List<Station> converToStations(List<Section> orderedSections) {
		List<Station> stations = new ArrayList<>();
		for(Section section : orderedSections){
			stations.add(section.getUpStation());
			stations.add(section.getDownStation());
		}
		return stations;
	}

	public List<Section> getOrderedSections(){
		Section startSection = findStartStation();
		return orderedSections(startSection);
	}

	private List<Section> orderedSections(Section startSection) {
		Map<Station, Section> upStationAndSectionRoute = getSectionRoute();
		List<Section> sections = new ArrayList<>();

		Section nextSection = startSection;
		while (nextSection!=null) {
			sections.add(nextSection);
			Station curDownStation = nextSection.getDownStation();
			nextSection = upStationAndSectionRoute.get(curDownStation);
		}
		return sections;
	}

	private Map<Station, Section> getSectionRoute() {
		return sections.stream()
			.collect(Collectors.toMap(Section::getUpStation,it->it, (o1, o2) -> o1, HashMap::new));
	}

	private Section findStartStation() {
		// 전체 하행역 콜렉션 생성
		Set<Station> downStations = sections.stream()
			.map(Section::getDownStation)
			.collect(Collectors.toSet());
		// 전체 상행역 중 하행역이 아닌 상행역 추출(=> 시작점)
		Optional<Section> startSection = sections.stream()
			.filter(it -> !downStations.contains(it.getUpStation()))
			.findFirst();
		return startSection.orElseThrow(() -> new IllegalStateException("구간 정보가 올바르지 않습니다."));
	}
}
