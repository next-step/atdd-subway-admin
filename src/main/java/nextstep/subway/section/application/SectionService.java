package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.lineStation.domain.LineStation;
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

    private final StationService stationService;
    private final SectionRepository sectionRepository;
    private final LineRepository lineRepository;


    public SectionService(SectionRepository sectionRepository,
                          StationService stationService,
                          LineRepository lineRepository) {
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
        this.lineRepository = lineRepository;
    }


    public SectionResponse saveSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationService.findStationById(sectionRequest.getUpStationId());
        Station downStation = stationService.findStationById(sectionRequest.getDownStationId());
        LineStation lineStation = sectionRequest.toLineStation(upStation, downStation);
        Line line = Line.getNotNullLine(lineRepository.findById(lineId));
        Section section = line.createNewSection(lineStation);
        line.addLineStation(lineStation);
        return SectionResponse.of(sectionRepository.save(section));
    }
}
