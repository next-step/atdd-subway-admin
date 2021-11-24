package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import nextstep.subway.common.ErrorCode;
import nextstep.subway.line.exception.SectionException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

@Embeddable
public class Sections {

	private static final int ONLY_ONE_SECTION_SIZE = 1;

	@OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
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
		if (!sectionList.isEmpty()) {
			sectionList.forEach(inner -> inner.validSection(section));
			sectionList
				.stream()
				.filter(inner -> !inner.isSameUpDownStation(section))
				.findAny()
				.ifPresent(inner -> inner.reSettingSection(section));
		}

		sectionList.add(section);
	}

	public List<StationResponse> getStations() {
		return getStationList().stream()
			.map(StationResponse::of)
			.collect(Collectors.toList());
	}

	public List<Station> getStationList() {
		return Stream.of(getUpStationList(), getDownStationList())
			.flatMap(Collection::stream)
			.distinct()
			.collect(Collectors.toList());
	}

	private List<Station> getUpStationList() {
		return sectionList.stream()
			.map(Section::getUpStation)
			.collect(Collectors.toList());
	}

	private List<Station> getDownStationList() {
		return sectionList.stream()
			.map(Section::getDownStation)
			.collect(Collectors.toList());
	}

	public void validRemoveStation(Station removeStation) {
		validSectionsSize();
		validStationsInStations(removeStation);
	}

	private void validStationsInStations(Station removeStation) {
		if (!getStationList().contains(removeStation)) {
			throw new SectionException(ErrorCode.VALID_CAN_NOT_REMOVE_NOT_IN_STATIONS);
		}
	}

	private void validSectionsSize() {
		if (sectionList.size() == ONLY_ONE_SECTION_SIZE) {
			throw new SectionException(ErrorCode.VALID_CAN_NOT_REMOVE_LAST_STATION);
		}
	}

	public void removeLineStation(Station removeStation) {
		validRemoveStation(removeStation);

		if (isBetweenStations(removeStation)) {
			removeBetweenStation(removeStation);
		}

		if (sectionList.size() != ONLY_ONE_SECTION_SIZE) {
			removeUpStation(removeStation);
			removeDownStation(removeStation);
		}
	}

	private void removeBetweenStation(Station removeStation) {
		Section removeSection = getRemoveSection(removeStation);
		sectionList.remove(removeSection);
		sectionList.stream()
			.findFirst()
			.ifPresent(inner -> inner.removeSection(removeSection));

	}

	private Section getRemoveSection(Station removeStation) {
		return sectionList.stream()
			.filter(inner -> inner.isSameUpStation(removeStation) || inner.isSameDownStation(removeStation))
			.findAny()
			.filter(inner -> inner.isSameUpStation(removeStation))
			.orElseThrow(NoSuchElementException::new);
	}

	private void removeUpStation(Station removeStation) {
		sectionList.stream()
			.filter(inner -> inner.isSameUpStation(removeStation))
			.findAny().ifPresent(inner -> sectionList.remove(inner));
	}

	private void removeDownStation(Station removeStation) {
		sectionList.stream()
			.filter(inner -> inner.isSameDownStation(removeStation))
			.findAny().ifPresent(inner -> sectionList.remove(inner));
	}

	public boolean isBetweenStations(Station removeStation) {
		return getUpStationList().contains(removeStation) && getDownStationList().contains(removeStation);
	}
}
