package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.lineStation.domain.LineStation;
import nextstep.subway.lineStation.domain.LineStationRepository;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
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
    private StationRepository stationRepository;
    private LineStationRepository lineStationRepository;

    public SectionService(LineRepository lineRepository, SectionRepository sectionRepository, StationRepository stationRepository, LineStationRepository lineStationRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
        this.lineStationRepository = lineStationRepository;
    }

    public SectionResponse saveSection(SectionRequest sectionRequest, Long lineId) {
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId()).orElseThrow(NoSuchElementException::new);
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId()).orElseThrow(NoSuchElementException::new);
        Distance distance = new Distance(sectionRequest.getDistance());
        Section persistSection = sectionRepository.save(sectionRequest.toSection(upStation, downStation, distance));
        Line line = lineRepository.findById(lineId).orElseThrow(NoSuchElementException::new);
        line.addSection(persistSection);

        LineStation upLineStation = lineStationRepository.findByLineIdAndStationId(line.getId(), upStation.getId()).orElse(new LineStation());
        line.addLineStation(upLineStation);
        upStation.addLineStation(upLineStation);

        LineStation downLineStation = lineStationRepository.findByLineIdAndStationId(line.getId(), downStation.getId()).orElse(new LineStation());
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
