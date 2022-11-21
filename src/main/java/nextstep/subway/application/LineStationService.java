package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineStationService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineStationService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public void addLineStation(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId()).orElseThrow(RuntimeException::new);
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId()).orElseThrow(RuntimeException::new);
        Line line = lineRepository.findById(lineId).orElseThrow(RuntimeException::new);
        line.addLineStation(sectionRequest.toLineStation(upStation, downStation));
    }
}
