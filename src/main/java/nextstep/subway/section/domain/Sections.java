package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.apache.commons.collections4.CollectionUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.exception.AlreadyExistException;
import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.exception.SectionDistanceException;
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
		int upMatchIndex = stations.indexOf(section.getUpStation());
		int downMatchIndex = stations.indexOf(section.getDownStation());
		validateMatch(upMatchIndex, downMatchIndex);
		if (upMatchIndex > -1 && downMatchIndex == -1) { // 상행역과 맞는경우
			setPostionUpMatch(section, stations, upMatchIndex);
		}
		if (upMatchIndex == -1 && downMatchIndex > -1) { // 하행역과 맞는경우
			setPositionDownMatch(section, stations, downMatchIndex);
		}
		// 다시 A B C D E ->  [A B] [B C] [C D] [D E] 섹션으로만든다.
		convertStationToSection(section.getLine(), stations);
	}

	private void validateMatch(int upMatchIndex, int downMatchIndex) {
		if (upMatchIndex > -1 && downMatchIndex > -1) {
			throw new AlreadyExistException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
		}
		if (upMatchIndex == -1 && downMatchIndex == -1) {
			throw new NotFoundException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.");
		}
	}

	private void convertStationToSection(Line line, List<Station> stations) {
		for (int i = 0; i < stations.size() - 1; i++) {
			Station upstation = stations.get(i);
			Station downStation = stations.get(i + 1);
			int distance = upstation.getNextDistance();
			if (sections.size() > i) {
				Section targetSection = sections.get(i);
				targetSection.update(upstation, downStation, distance);
				continue;
			}
			sections.add(Section.create(line, upstation, downStation, distance));
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
			validateDistance(targetBeforeDistance);
			targetBeforeStation.updateNextDistance(targetBeforeDistance);
		}
	}

	private void setPostionUpMatch(Section section, List<Station> stations, int upMatchIndex) {
		Station standardStation = stations.get(upMatchIndex);
		Station targetStation = section.getDownStation();
		if (!standardStation.isFinalStation()) {
			int standardDistance = standardStation.getNextDistance();
			int targetDistance = standardDistance - section.getDistance();
			validateDistance(targetDistance);
			targetStation.updateNextDistance(targetDistance);
		}
		standardStation.updateNextDistance(section.getDistance());
		stations.add(upMatchIndex + 1, targetStation);
	}

	private void validateDistance(int targetDistance) {
		if (targetDistance <= 0) {
			throw new SectionDistanceException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
		}
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

	public void removeSectionByStation(Line line, Station targetStation) {
		List<Station> stations = convertSectionToStation();
		int targetIndex = stations.indexOf(targetStation);
		if (targetIndex == 0) {
			stations.remove(0);
		}
		if (targetIndex > 0) {
			Station preStation = stations.get(targetIndex - 1);
			preStation.sumNextDistance(targetStation);
			stations.remove(targetIndex);
		}
		updateLastStationDistanceZero(stations);
		removeLastSection();
		convertStationToSection(line, stations);
	}

	private void updateLastStationDistanceZero(List<Station> stations) {
		Station lastStation = stations.get(stations.size() - 1);
		lastStation.updateNextDistance(0);
	}

	private void removeLastSection() {
		this.sections.remove(this.sections.size() - 1);
	}
}
