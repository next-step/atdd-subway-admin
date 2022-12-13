package nextstep.subway.application;

import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SectionService {
    private final StationService stationService;
    private final LineService lineService;

    public SectionService(StationService stationService, LineService lineService) {
        this.stationService = stationService;
        this.lineService = lineService;
    }

    @Transactional
    public LineResponse addSection(Long id, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        Line line = lineService.findById(id);
        line.addSection(Section.of(upStation, downStation, Distance.from(sectionRequest.getDistance())));
        return LineResponse.of(line);
    }

    @Transactional
    public void removeSectionByStationId(Long lineId, Long stationId) {
        Line line = lineService.findById(lineId);
        Station station = stationService.findById(stationId);
        line.removeSection(station);
    }
}
