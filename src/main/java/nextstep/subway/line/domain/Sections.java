package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line")
	private List<Section> sectionList = new ArrayList<>();

	protected Sections() {
	}

	public Sections(final List<Section> sectionList) {
		this.sectionList = Collections.unmodifiableList(sectionList);
	}

	public List<Section> getSectionList() {
		return sectionList;
	}

	public void add(Section section) {
		sectionList.add(section);
	}

	public List<Station> getStations() {
		Set<Station> upStations = sectionList.stream().map(Section::getUpStation).collect(Collectors.toSet());
		Set<Station> downStations = sectionList.stream().map(Section::getDownStation).collect(Collectors.toSet());

		return Stream.of(upStations, downStations)
			.flatMap(Collection::stream).distinct().collect(Collectors.toList());
	}
}
