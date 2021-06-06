package nextstep.subway.section.application;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.application.StationQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionCommandService {

    private final StationQueryService stationQueryService;
    private final SectionRepository sectionRepository;

    public SectionCommandService(StationQueryService stationQueryService,
                                 SectionRepository sectionRepository) {
        this.stationQueryService = stationQueryService;
        this.sectionRepository = sectionRepository;
    }

    public Long save(Long upStationId, Long downStationId, int distance) {
        Section entity = new Section(stationQueryService.findStationById(upStationId),
                                     stationQueryService.findStationById(downStationId),
                                     distance);

        return sectionRepository.save(entity).getId();
    }
}
