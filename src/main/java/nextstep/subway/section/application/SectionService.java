package nextstep.subway.section.application;

import nextstep.subway.enums.SectionAddType;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.lineStation.domain.LineStation;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.wrappers.Distance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


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
        Line line = Line.getNotNullLine(lineRepository.findById(lineId));
        LineStation lineStation = sectionRequest.toLineStation(upStation, downStation);
        line.checkValidLineStation(lineStation);
        SectionAddType sectionAddType = line.calcAddType(lineStation);
        Optional<LineStation> updateTargetLineStation = line.updateLineStationAndSection(sectionAddType, lineStation);
        if (updateTargetLineStation.isPresent()) {
            LineStation updateLineStation = updateTargetLineStation.get();
            Section findSection = sectionRepository.findByLineAndDownStation(line, updateLineStation.getStation());
            Distance newDistance = new Distance(updateLineStation.getDistance().subtractionDistance(lineStation.getDistance()));
            updateLineStation.update(updateLineStation.getStation(), lineStation.getStation(), newDistance);
            findSection.update(line, updateLineStation.getPreStation(), findSection.downStation(), newDistance);
        }

        line.addLineStation(lineStation);
        return SectionResponse.of(sectionRepository.save(sectionRequest.toSection(line, upStation, downStation)));
    }
}
