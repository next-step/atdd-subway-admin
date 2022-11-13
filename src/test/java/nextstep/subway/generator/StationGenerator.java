package nextstep.subway.generator;

import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;

public class StationGenerator {

	private final StationRepository stationRepository;

	public StationGenerator(StationRepository stationRepository) {
		this.stationRepository = stationRepository;
	}

	public static Station station(String name) {
		return Station.from(name);
	}

	public Station savedStation(String name) {
		return stationRepository.saveAndFlush(station(name));
	}
}
