package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private final LineService lineService;
    private final StationService stationService;
    private final SectionRepository sectionRepository;

    public SectionService(LineService lineService,
                          StationService stationService,
                          SectionRepository sectionRepository) {
        this.lineService = lineService;
        this.stationService = stationService;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest request) {
        Line line = lineService.findById(lineId);
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());
        Section section = new Section(upStation, downStation, request.getDistance());
        line.addSection(section);
        sectionRepository.save(section);
    }
}
