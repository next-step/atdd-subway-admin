package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
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
    private SectionRepository sectionRepository;
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public SectionService(SectionRepository sectionRepository, LineRepository lineRepository, StationRepository stationRepository) {
        this.sectionRepository = sectionRepository;
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public SectionResponse saveSection(SectionRequest sectionRequest, Long lineId) {
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId()).orElseThrow(NoSuchElementException::new);
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId()).orElseThrow(NoSuchElementException::new);
        Section section = sectionRequest.toSection(upStation, downStation, new Distance(sectionRequest.getDistance()));
        Line line = lineRepository.findById(lineId).orElseThrow(NoSuchElementException::new);
        line.addSection(section);

        Section persistSection = sectionRepository.save(section);
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
        Line line = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
        List<Section> sections = line.getSections().getSection();
        return sections.stream()
                .map(it -> SectionResponse.of(it))
                .collect(Collectors.toList());
    }

    public void deleteByLineId(Long id) {
        sectionRepository.deleteByLineId(id);
    }
}
