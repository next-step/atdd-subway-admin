package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SectionService {
    private final LineService lineService;
    private final StationService stationService;

    public SectionService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @Transactional
    public void saveSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());

        Sections sections = lineService.findById(lineId).getSections();
        sections.updateSection(upStation, downStation, sectionRequest.getDistance());

        Line line = lineService.findById(lineId);
        line.addSection(new Section(upStation, downStation, new Distance(sectionRequest.getDistance())));
    }
}
