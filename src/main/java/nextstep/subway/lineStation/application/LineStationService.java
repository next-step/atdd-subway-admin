package nextstep.subway.lineStation.application;

import nextstep.subway.lineStation.domain.LineStation;
import nextstep.subway.lineStation.domain.LineStationRepository;
import nextstep.subway.lineStation.dto.LineStationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineStationService {
    private LineStationRepository lineStationRepository;

    public LineStationService(LineStationRepository lineStationRepository) {
        this.lineStationRepository = lineStationRepository;
    }

    public List<LineStationResponse> findAllLineStations() {
        List<LineStation> lineStations = lineStationRepository.findAll();
        return lineStations.stream()
                .map(it -> LineStationResponse.of(it))
                .collect(Collectors.toList());
    }

    public List<LineStationResponse> findByStationId(Long id) {
        List<LineStation> lineStations = lineStationRepository.findByStationId(id);
        return lineStations.stream()
                .map(it -> LineStationResponse.of(it))
                .collect(Collectors.toList());
    }
}
