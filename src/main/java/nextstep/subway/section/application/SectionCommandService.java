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

    public SectionCommandService(StationQueryService stationQueryService, SectionRepository sectionRepository) {
        this.stationQueryService = stationQueryService;
        this.sectionRepository = sectionRepository;
    }

    public Long save(Long upStationId, Long downStationId, int distance) {
        Section entity = new Section(stationQueryService.findStationById(upStationId),
                                     stationQueryService.findStationById(downStationId),
                                     distance);

        return sectionRepository.save(entity).getId();
    }

//    public LineResponse addSection(Long lineId, SectionRequest request) {
//        Line line = lineQueryService.findLineById(lineId);
//        return saveLineAndSection(line, request.getUpStationId(), request.getDownStationId(), request.getDistance());
//    }
//
//    private LineResponse saveLineAndSection(Line line, Long upStationId, Long downStationId, int distance) {
//        Station upStation = stationQueryService.findStationById(upStationId);
//        Station downStation = stationQueryService.findStationById(downStationId);
//
//        Section section = new Section(upStation, downStation, distance);
//        line.addSection(section);
//        sectionCommandService.saveSection(section);
//
//        return LineResponse.of(lineRepository.save(line));
//    }
}
