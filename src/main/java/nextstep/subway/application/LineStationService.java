package nextstep.subway.application;

import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.LineStationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineStationService {
    private LineStationRepository lineStationRepository;

    public LineStationService(LineStationRepository lineStationRepository) {
        this.lineStationRepository = lineStationRepository;
    }

    public LineStation saveLineStation(LineStation lineStation) {
        return lineStationRepository.save(lineStation);
    }
}
