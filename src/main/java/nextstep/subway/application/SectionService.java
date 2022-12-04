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
@Transactional
public class SectionService {

    private StationRepository stationRepository;
    private LineRepository lineRepository;

    public SectionService(StationRepository stationRepository, LineRepository lineRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
    }

    public void addSection(long lineId, SectionRequest section) {
        Line line = lineRepository.getById(lineId);
        Station upStation = stationRepository.getById(section.getUpStationId());
        Station downStation = stationRepository.getById(section.getDownStationId());
        line.addSection(Section.of(upStation, downStation, section.getDistance()));
    }
}
