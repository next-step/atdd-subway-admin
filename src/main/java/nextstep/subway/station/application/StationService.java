package nextstep.subway.station.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@Service
@Transactional
public class StationService {
    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationResponse saveStation(StationRequest stationRequest) {
        try {
            Station persistStation = stationRepository.save(stationRequest.toStation());
            return persistStation.toStationResponse();
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateKeyException("역 생성에 실패했습니다. 이미 존재하는 역입니다.");
        }
    }

    public List<StationResponse> saveAllStations(List<StationRequest> stationRequests) {
        List<Station> stations = stationRequests.stream()
                .map(req -> req.toStation())
                .collect(Collectors.toList());
        return this.stationRepository.saveAll(stations)
                .stream()
                .map(Station::toStationResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAllStations() {
        List<Station> stations = stationRepository.findAll();

        return stations.stream()
                .map(station -> station.toStationResponse())
                .collect(Collectors.toList());
    }

    public void deleteStationById(Long id) {
        Station station = findStationByIdOrThrow(id, "삭제 대상 역이 존재하지 않습니다.");
        stationRepository.delete(station);
    }

    private Station findStationByIdOrThrow(Long id, String throwMessage) {
        return stationRepository.findById(id).orElseThrow(() -> new NoSuchElementException(throwMessage));
    }
}
