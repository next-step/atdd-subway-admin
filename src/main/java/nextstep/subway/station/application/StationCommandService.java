package nextstep.subway.station.application;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StationCommandService {

    private final StationRepository stationRepository;

    public StationCommandService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Long save(Station nonPersistStation) {
        return stationRepository.save(nonPersistStation).getId();
    }

    public void deleteById(Long id) {
        stationRepository.deleteById(id);
    }
}
