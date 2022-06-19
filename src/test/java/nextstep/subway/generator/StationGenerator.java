package nextstep.subway.generator;

import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@TestConstructor(autowireMode = AutowireMode.ALL)
public class StationGenerator {

    private final StationRepository stationRepository;

    public StationGenerator(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station savedStation(Station station) {
        return stationRepository.saveAndFlush(station);
    }
}
