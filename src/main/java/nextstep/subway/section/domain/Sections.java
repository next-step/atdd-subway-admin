package nextstep.subway.section.domain;

import nextstep.subway.exception.ApiException;
import nextstep.subway.section.application.SortedSectionUtil;
import nextstep.subway.station.domain.Station;
import org.springframework.util.CollectionUtils;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.exception.ApiExceptionMessge.EXISTS_ALL_STATIONS;
import static nextstep.subway.exception.ApiExceptionMessge.NOT_EXISTS_STATIONS;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	public void add(final Section section) {
		if (CollectionUtils.isEmpty(sections) == false) {
			validateStations(section);
			connectIfExistsUpStation(section);
			connectIfExistsDownStation(section);
		}
		this.sections.add(section);
	}

	public boolean contain(final Section section) {
		return sections.contains(section);
	}

	private void validateStations(final Section section) {
		boolean isExistsUpStation = containUpStation(section);
		boolean isExistsDownStation = containDownStation(section);
		if (isExistsUpStation && isExistsDownStation) {
			throw new ApiException(EXISTS_ALL_STATIONS);
		}
		if (isExistsUpStation == false && isExistsDownStation == false) {
			throw new ApiException(NOT_EXISTS_STATIONS);
		}
	}

	private void connectIfExistsUpStation(final Section section) {
		this.sections.stream()
					 .filter(value -> value.upStation().equals(section.upStation()))
					 .findFirst()
					 .ifPresent(value -> value.connectUpStationToDownStation(section));
	}

	private void connectIfExistsDownStation(final Section section) {
		this.sections.stream()
					 .filter(value -> value.downStation().equals(section.downStation()))
					 .findFirst()
					 .ifPresent(value -> value.connectDownStationToUpStation(section));
	}

	private boolean containUpStation(final Section section) {
		return stations().contains(section.upStation());
	}

	private boolean containDownStation(final Section section) {
		return stations().contains(section.downStation());
	}

	private List<Station> stations() {
		return this.sections.stream()
							.flatMap(Section::streamOfStation)
							.distinct()
							.collect(Collectors.toList());
	}

	public List<Station> stationsBySorted() {
		return SortedSectionUtil.of(this.sections)
								.sorted();
	}

}
