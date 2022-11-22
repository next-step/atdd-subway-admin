package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;

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
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId()).orElseThrow(NoResultException::new);
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId()).orElseThrow(NoResultException::new);
        Line line = lineRepository.findById(lineId).orElseThrow(NoResultException::new);
        line.addLineStation(sectionRequest.toLineStation(upStation, downStation));
    }
}
