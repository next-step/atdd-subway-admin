package nextstep.subway.section.application;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionCreateRequest;
import nextstep.subway.section.dto.SectionCreateResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class SectionService {

    public static final int START_SEQUENCE = 0;
    public static final int STEP = 1;
    private StationService stationService;
    private SectionRepository sectionRepository;

    public SectionService(StationService stationService, SectionRepository sectionRepository) {
        this.stationService = stationService;
        this.sectionRepository = sectionRepository;
    }

    public SectionCreateResponse saveSection(SectionCreateRequest request) {
        Station up = stationService.getOne(request.getUpStationId());
        Station down = stationService.getOne(request.getDownStationId());
        int order = getNextSequence();
        Section section = sectionRepository.save(new Section(request.getLine(), up, down, request.getDistance(), order));
        return SectionCreateResponse.of(section);
    }

    private int getNextSequence() {
        Section section = sectionRepository.findFirstByOrderBySequenceDesc();
        if (section == null) {
            return START_SEQUENCE;
        }
        return section.getSequence() + STEP;
    }
}
