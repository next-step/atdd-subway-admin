package nextstep.subway.application;

import nextstep.subway.domain.LineStation.LineStation;
import nextstep.subway.domain.LineStation.LineStationRepository;
import nextstep.subway.domain.LineStation.LineStations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LineStationService {
    private final LineStationRepository lineStationRepository;

    public LineStationService(LineStationRepository lineStationRepository) {
        this.lineStationRepository = lineStationRepository;
    }

    public LineStations getLineStationsByStationId(Long stationId) {
        List<LineStation> lineStations = lineStationRepository.findByStationId(stationId);

        if (lineStations.isEmpty()) {
            throw new IllegalArgumentException("해당역을 포함하는 노선이 존재하지 않습니다. stationId : " + stationId);
        }

        return LineStations.create(lineStations);
    }
}
