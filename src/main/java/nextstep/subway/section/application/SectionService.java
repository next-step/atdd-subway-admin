package nextstep.subway.section.application;

import nextstep.subway.lineStation.domain.LineStationRepository;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class SectionService {

    private final StationService stationService;
    private final SectionRepository sectionRepository;
    private final LineStationRepository lineStationRepository;

    public SectionService(SectionRepository sectionRepository,
                          StationService stationService,
                          LineStationRepository lineStationRepository) {
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
        this.lineStationRepository = lineStationRepository;
    }


    public void saveSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationService.findStationById(sectionRequest.getUpStationId());
        Station downStation = stationService.findStationById(sectionRequest.getDownStationId());
    }
}
