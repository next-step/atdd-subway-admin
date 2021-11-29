package nextstep.subway.section.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionService {
    private final SectionRepository sectionRepository;
    private final StationService stationService;
    private final LineService lineService;

    public SectionService(SectionRepository sectionRepository, StationService stationService, LineService lineService) {
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
        this.lineService = lineService;
    }

    public SectionResponse saveSection(SectionRequest sectionRequest, Long lineId) {
        Station upStation = stationService.findByIdOrElseThrow(sectionRequest.getUpStationId());
        Station downStation = stationService.findByIdOrElseThrow(sectionRequest.getDownStationId());
        Section section = sectionRepository.save(new Section(upStation, downStation, sectionRequest.getDistance()));
        section.setLine(lineService.findByIdOrElseThrow(lineId));
        return SectionResponse.of(section);
    }
}
