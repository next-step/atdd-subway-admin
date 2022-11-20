package nextstep.subway.application;

import nextstep.subway.domain.ErrorMessage;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionService {

    private SectionRepository sectionRepository;
    private LineService lineService;
    private StationService stationService;

    public SectionService(SectionRepository sectionRepository, LineService lineService, StationService stationService) {
        this.sectionRepository = sectionRepository;
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public SectionResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineService.getLineById(lineId);
        if(sectionRequest.getDistance() <= 0) {
            throw new IllegalArgumentException("");
        }
        if(line.compareToDistance(sectionRequest.getDistance()) <= 0) {
            throw new IllegalArgumentException("");
        }
        Section persistSection = sectionRepository.save(new Section(
                stationService.getStationById(sectionRequest.getUpStationId()),
                stationService.getStationById(sectionRequest.getDownStationId()),
                line, sectionRequest.getDistance()));
        line.addSection(persistSection);
        return SectionResponse.of(persistSection);
    }
}
