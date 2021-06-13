package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.lineStation.application.LineStationService;
import nextstep.subway.lineStation.domain.LineStation;
import nextstep.subway.lineStation.domain.LineStationRepository;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class SectionService {
    private LineRepository lineRepository;
    private SectionRepository sectionRepository;
    private final StationService stationService;
    private final LineStationService lineStationService;

    public SectionService(LineRepository lineRepository, SectionRepository sectionRepository, final StationService stationService, final LineStationService lineStationService) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
        this.lineStationService = lineStationService;
    }

    public SectionResponse saveSection(SectionRequest sectionRequest, Long lineId) {
        Station upStation = stationService.findStationById(sectionRequest.getUpStationId());
        Station downStation = stationService.findStationById(sectionRequest.getDownStationId());
        Section section = sectionRequest.toSection(upStation, downStation, new Distance(sectionRequest.getDistance()));
        Section persistSection = sectionRepository.save(sectionRequest.toSection(upStation, downStation, new Distance(sectionRequest.getDistance())));
        Line line = lineRepository.findById(lineId).orElseThrow(NoSuchElementException::new);
        line.addSection(persistSection);

        LineStation upLineStation = lineStationService.findByLineIdAndStationId(line, upStation);
        line.addLineStation(upLineStation);
        upStation.addLineStation(upLineStation);

        LineStation downLineStation = lineStationService.findByLineIdAndStationId(line, downStation);
        line.addLineStation(downLineStation);
        downStation.addLineStation(downLineStation);
        return SectionResponse.of(persistSection);
    }

    public SectionResponse findById(Long id) {
        return SectionResponse.of(sectionRepository.findById(id).orElseThrow(NoSuchElementException::new));
    }

    public List<SectionResponse> findAllSections() {
        List<Section> sections = sectionRepository.findAll();
        return sections.stream()
                .map(it -> SectionResponse.of(it))
                .collect(Collectors.toList());
    }
}
