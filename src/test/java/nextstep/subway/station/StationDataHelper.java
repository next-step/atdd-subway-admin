package nextstep.subway.station;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Component;

@Component
public class StationDataHelper {
    private final StationRepository stationRepository;

    public StationDataHelper(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Long 역추가(String name) {
        Station save = stationRepository.save(new Station(name));
        return save.getId();
    }
}
