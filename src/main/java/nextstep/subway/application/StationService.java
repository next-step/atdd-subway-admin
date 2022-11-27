package nextstep.subway.application;

import nextstep.subway.domain.SectionRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.domain.Stations;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StationService {
    private StationRepository stationRepository;
    private SectionRepository sectionRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    public StationResponse saveStation(StationRequest stationRequest) {
        Station persistStation = stationRepository.save(stationRequest.toStation());
        return StationResponse.of(persistStation);
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAllStations() {
        List<Station> stations = stationRepository.findAll();

        return stations.stream()
                .map(station -> StationResponse.of(station))
                .collect(Collectors.toList());
    }

    public Stations findAllById(List<Long> stationIds) {
        List<Station> stationList = stationRepository.findAllById(stationIds);
        if (stationList.size() != stationIds.size()) {
            throw new IllegalStateException("존재하지 않는 지하철역이 포함되어 있습니다");
        }
        return new Stations(stationList);
    }

    @Transactional
    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }

    public Station findById(long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철역입니다. 요청ID:" + stationId));
    }

    public void deleteIfNotContainsAnySection(Station station) {
        /* 역이 어떤 구간에도 포함되어 있지 않은 경우 삭제 */
        if (!sectionRepository.existsAllByUpStationOrDownStation(station, station)) {
            stationRepository.delete(station);
        }
    }
}
