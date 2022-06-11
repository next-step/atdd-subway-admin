package nextstep.subway.application;

import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
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
    public LineResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line findLine = lineService.findById(lineId);
        Station upStation = stationService.findStationById(sectionRequest.getUpStationId());
        Station downStation = stationService.findStationById(sectionRequest.getDownStationId());
        Section section = Section.of(upStation, downStation, new Distance(sectionRequest.getDistance()));
        Sections sections = findLine.getSections();

        validate(sections, section);

        findLine.addSection(Section.of(upStation, downStation, new Distance(sectionRequest.getDistance())));

        return LineResponse.of(findLine);
    }

    private void validate(Sections sections, Section section) {
        if (!sections.isEmpty()) {
            sections.validateDuplicate(section);
            sections.validateExistence(section);
        }
    }

}
