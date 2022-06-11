package nextstep.subway.application;

import javassist.NotFoundException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SectionService {

    private LineService lineService;
    private StationService stationService;

    public SectionService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse addSection(SectionRequest sectionRequest, Long lineId)
        throws NotFoundException {
        Line line = lineService.findLineOrThrow(lineId);
        Station upStation = stationService.findStationOrThrow(sectionRequest.getUpStationId());
        Station downStation = stationService.findStationOrThrow(sectionRequest.getDownStationId());

        line.updateSection(new Section(upStation, downStation, sectionRequest.getDistance()));
        return LineResponse.of(line);
    }

    @Transactional
    public void removeSection(Long stationId, Long lineId) throws NotFoundException {
        Line line = lineService.findLineOrThrow(lineId);
        Station station = stationService.findStationOrThrow(stationId);
        line.removeSectionByStation(station);
    }
}
