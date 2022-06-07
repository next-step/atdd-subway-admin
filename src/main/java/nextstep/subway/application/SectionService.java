package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
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
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line findLine = lineService.findById(lineId);
        Station upStation = stationService.findStationById(sectionRequest.getUpStationId());
        Station downStation = stationService.findStationById(sectionRequest.getDownStationId());
        Section section = Section.of(upStation, downStation, sectionRequest.getDistance());
        Sections sections = findLine.getSections();

        validate(sections, section);

        findLine.addSection(Section.of(upStation, downStation, sectionRequest.getDistance()));
    }

    private void validate(Sections sections, Section section) {
        if (!sections.isEmpty()) {
            sections.validateDuplicate(section);
            sections.validateExistence(section);
        }
    }
}
