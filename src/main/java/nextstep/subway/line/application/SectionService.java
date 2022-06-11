package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionAddRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SectionService{
    private final StationService stationService;
    private final LineService lineService;

    public SectionService(final StationService stationService, final LineService lineService) {
        this.stationService = stationService;
        this.lineService = lineService;
    }

    @Transactional
    public LineResponse addSection(final Long id, final SectionAddRequest sectionAddRequest) {
        final Station upStation = stationService.findById(sectionAddRequest.getUpStationId());
        final Station downStation = stationService.findById(sectionAddRequest.getDownStationId());
        final Line line = lineService.findById(id);

        line.addSection(Section.of(upStation, downStation, sectionAddRequest.getDistance()));
        return LineResponse.from(line);
    }
}
