package nextstep.subway.application;

import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import nextstep.subway.repository.SectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SectionService {
    private final LineService lineService;
    private final StationService stationService;
    private final SectionRepository sectionRepository;

    public SectionService(LineService lineService, StationService stationService, SectionRepository sectionRepository) {
        this.lineService = lineService;
        this.stationService = stationService;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public SectionResponse saveSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        Section section = new Section(upStation, downStation, new Distance(sectionRequest.getDistance()));

        Line line = lineService.findById(lineId);
        line.addSection(section);
        sectionRepository.flush();
        return SectionResponse.of(section);
    }
}
