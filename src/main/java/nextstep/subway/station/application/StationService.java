package nextstep.subway.station.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@Service
@Transactional
public class StationService {
    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationResponse save(StationRequest request) {
        checkDuplicatedName(request.getName());
        
        return StationResponse.of(stationRepository.save(request.toStation()));
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAll() {
        return stationRepository.findAll()
                .stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StationResponse find(Long id) {
        return StationResponse.of(findById(id));
    }
    
    public void update(Long id, StationRequest request) {
        Station station = findById(id);
        station.update(request.toStation());
    }
    
    public void delete(Long id) {
        Station station = findById(id);
        stationRepository.delete(station);
    }
    
    
    @Transactional(readOnly = true)
    public Station findById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + "에 해당하는 지하철역이 없습니다."));
    }
    
    private void checkDuplicatedName(String name) {
        if (stationRepository.existsByName(name)) {
            throw new IllegalArgumentException(String.format("지하철역 이름(%d)이 중복되었습니다.", name));
        }
    }
}
