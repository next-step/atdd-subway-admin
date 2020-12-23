package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.apache.commons.collections4.CollectionUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.station.domain.Station;

@Getter
@Embeddable
@NoArgsConstructor
public class Sections {

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	public void initSection(Section section) {
		sections.add(section);
	}

	public void add(Section section) {
		//[A B] [B C] [C D] [D E]  -> A B C D E
		List<Station> stations = convertSectionToStation();
		int upMatchIndex = findMatchIndex(stations, section.getUpStation());
		int downMatchIndex = findMatchIndex(stations, section.getDownStation());

		if (upMatchIndex > -1 && downMatchIndex == -1) { // 상행역과 맞는경우
			setPostionUpMatch(section, stations, upMatchIndex);
		}
		if (upMatchIndex == -1 && downMatchIndex > -1) { // 하행역과 맞는경우
			setPositionDownMatch(section, stations, downMatchIndex);
		}
		// 다시 A B C D E ->  [A B] [B C] [C D] [D E] 섹션으로만든다.
		convertStationToSection(section, stations);
	}

	private void convertStationToSection(Section section, List<Station> stations) {
		for (int i = 0; i < stations.size() - 1; i++) {
			Station upstation = stations.get(i);
			Station downStation = stations.get(i + 1);
			int distance = upstation.getNextDistance();
			if (sections.size() > i) {
				Section targetSection = sections.get(i);
				targetSection.update(upstation, downStation, distance);
				continue;
			}
			sections.add(Section.create(section.getLine(), upstation, downStation, distance));
		}
	}

	private void setPositionDownMatch(Section section, List<Station> stations, int downMatchIndex) {
		Station targetStation = section.getUpStation();
		int targetDistance = section.getDistance();
		targetStation.updateNextDistance(targetDistance);
		stations.add(downMatchIndex, targetStation);

		if (downMatchIndex > 0) {
			Station targetBeforeStation = stations.get(downMatchIndex - 1);
			int targetBeforeDistance = targetBeforeStation.getNextDistance() - targetDistance;
			targetBeforeStation.updateNextDistance(targetBeforeDistance);
		}
	}

	private void setPostionUpMatch(Section section, List<Station> stations, int upMatchIndex) {
		Station standardStation = stations.get(upMatchIndex);
		Station targetStation = section.getDownStation();
		if (!standardStation.isFinalStation()) {
			int standardDistance = standardStation.getNextDistance();
			int targetDistance = standardDistance - section.getDistance();
			targetStation.updateNextDistance(targetDistance);
		}
		standardStation.updateNextDistance(section.getDistance());
		stations.add(upMatchIndex + 1, targetStation);
	}

	private int findMatchIndex(List<Station> stations, Station station) {
		return IntStream.range(0, stations.size())
			.filter(i -> station.equals(stations.get(i)))
			.findFirst()
			.orElse(-1);
	}

	public List<Station> convertSectionToStation() {
		return CollectionUtils.emptyIfNull(this.sections).stream()
			.map(section -> {
				Station upstation = section.getUpStation();
				Station downStation = section.getDownStation();
				upstation.updateNextDistance(section.getDistance());
				return Arrays.asList(upstation, downStation);
			})
			.flatMap(Collection::stream)
			.distinct()
			.collect(Collectors.toCollection(ArrayList::new));
	}
}
