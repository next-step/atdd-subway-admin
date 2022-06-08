package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SectionService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public SectionService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest request) {
        Line line = lineRepository.getById(lineId);
        Station upStation = stationRepository.getById(request.getUpStationId());
        Station downStation = stationRepository.getById(request.getDownStationId());
        line.addSection(new Section(upStation, downStation, request.getDistance()));
    }

    @Transactional
    public void removeSectionByStationId(Long lineId, Long stationId) {
        Line targetLine = lineRepository.getById(lineId);
        Station targetStation = stationRepository.getById(stationId);
        targetLine.removeSectionByDownStation(targetStation);
    }
}
