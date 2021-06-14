package nextstep.subway.section.application;

import nextstep.subway.enums.SectionAddType;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.LineStation;
import nextstep.subway.section.domain.LineStationRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class SectionService {

    private final StationService stationService;
    private final LineRepository lineRepository;
    private final LineStationRepository lineStationRepository;


    public SectionService(StationService stationService,
                          LineRepository lineRepository,
                          LineStationRepository lineStationRepository) {
        this.stationService = stationService;
        this.lineRepository = lineRepository;
        this.lineStationRepository = lineStationRepository;
    }


    public SectionResponse saveSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationService.findStationById(sectionRequest.getUpStationId());
        Station downStation = stationService.findStationById(sectionRequest.getDownStationId());
        Line line = Line.getNotNullLine(lineRepository.findById(lineId));
        LineStation lineStation = sectionRequest.toLineStation(upStation, downStation);
        line.checkValidLineStation(lineStation);
        SectionAddType sectionAddType = line.calcAddType(lineStation);
        line.updateLineStation(sectionAddType, lineStation);
        line.addLineStation(lineStation);
        return SectionResponse.of(lineStationRepository.save(lineStation));
    }
}
