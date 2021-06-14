package nextstep.subway.section.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.lineStation.application.LineStationService;
import nextstep.subway.lineStation.domain.LineStation;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class SectionService {
    private SectionRepository sectionRepository;
    private final LineService lineService;
    private final StationService stationService;
    private final LineStationService lineStationService;

    public SectionService(SectionRepository sectionRepository, final LineService lineService, final StationService stationService, final LineStationService lineStationService) {
        this.sectionRepository = sectionRepository;
        this.lineService = lineService;
        this.stationService = stationService;
        this.lineStationService = lineStationService;
    }

    public SectionResponse saveSection(SectionRequest sectionRequest, Long lineId) {
        Station upStation = stationService.findStationById(sectionRequest.getUpStationId());
        Station downStation = stationService.findStationById(sectionRequest.getDownStationId());
        Section section = sectionRequest.toSection(upStation, downStation, new Distance(sectionRequest.getDistance()));
        Line line = lineService.findLineById(lineId);
        line.addSection(section);

        Section persistSection = sectionRepository.save(section);
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

    public List<SectionResponse> findSectionsByLineId(Long id) {
        Line line = lineService.findLineById(id);
        List<Section> sections = line.getSections().getSection();
        return sections.stream()
                .map(it -> SectionResponse.of(it))
                .collect(Collectors.toList());
    }
}
