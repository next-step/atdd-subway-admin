package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
	@OneToMany(
		cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
		fetch = FetchType.LAZY,
		mappedBy = "line",
		orphanRemoval = true)
	private List<Section> values = new ArrayList<>();

	protected Sections() {

	}

	private Sections(List<Section> values) {
		this.values = values;
	}

	public static Sections of(List<Section> values) {
		return new Sections(values);
	}

	public void add(Section value) {
		values.add(value);
	}

	public List<Station> getStations() {
		if (isEmpty()) {
			return Collections.emptyList();
		}

		List<Station> stations = new ArrayList<>();
		Map<Station, Station> downStationByUpStation = getDownStationByUpStation();
		Station upStation = getHeadUpStation();
		addStationsSequentially(stations, downStationByUpStation, upStation);

		return stations;
	}

	private void addStationsSequentially(
		List<Station> stations,
		Map<Station, Station> downStationByUpStation,
		Station upStation
	) {
		while (upStation != null) {
			stations.add(upStation);
			upStation = downStationByUpStation.get(upStation);
		}
	}

	private boolean isEmpty() {
		return values == null || values.isEmpty();
	}

	private Map<Station, Station> getDownStationByUpStation() {
		return values.stream()
			.collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
	}

	private Station getHeadUpStation() {
		List<Station> downStations = values.stream()
			.map(Section::getDownStation)
			.collect(Collectors.toList());

		return values.stream()
			.map(Section::getUpStation)
			.filter(upStation -> !downStations.contains(upStation))
			.findFirst()
			.orElseThrow(IllegalStateException::new);
	}

	public int size() {
		return values.size();
	}
}
