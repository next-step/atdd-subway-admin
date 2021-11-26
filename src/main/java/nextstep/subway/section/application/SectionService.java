package nextstep.subway.section.application;

import nextstep.subway.section.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionService {

    private final SectionRepository sectionRepository;
    private final StationService stationService;

    public SectionService(SectionRepository sectionRepository,
        StationService stationService) {
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
    }

    public Section saveSection(final SectionRequest request, final Line persistLine) {
        final Station upStation = stationService.findStationById(request.getUpStationId());
        final Station downStation = stationService.findStationById(request.getDownStationId());

        return sectionRepository.save(new Section(upStation, downStation, request.toDistance())).withLine(persistLine);
    }
}
