package nextstep.subway.station.application;

import nextstep.subway.common.exception.DataNotFoundException;
import nextstep.subway.common.message.ExceptionMessage;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StationQueryService {
    private StationRepository stationRepository;

    public StationQueryService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public List<StationResponse> findAllStations() {
        List<Station> stations = stationRepository.findAll();

        return stations.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    public Station findById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(ExceptionMessage.NOT_FOUND_STATION));
    }
}
