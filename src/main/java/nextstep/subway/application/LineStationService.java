package nextstep.subway.application;

import nextstep.subway.domain.Line;
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

    public void saveLineStation(LineStation lineStation) {
        lineStationRepository.save(lineStation);
    }

    public void deleteLineStationByLineId(Long lineId) {
        lineStationRepository.deleteByLineId(lineId);
    }
}
