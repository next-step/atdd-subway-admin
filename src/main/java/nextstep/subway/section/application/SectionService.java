package nextstep.subway.section.application;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
@Transactional
public class SectionService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public SectionService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lineId)
            .orElseThrow(EntityNotFoundException::new);
        List<Section> sections = line.getOrderedLineSections();
        Section newSection = getSection(sectionRequest);
        Optional<Section> containSection = getContainsSection(sections, newSection);

        if (containSection.isPresent()) {
            containSection.get().updateSection(newSection);
        }
        line.addLineSection(newSection);
    }

    private Section getSection(SectionRequest request) {
        Station upStation = stationRepository.findById(request.getUpStationId())
            .orElseThrow(EntityNotFoundException::new);
        Station downStation = stationRepository.findById(request.getDownStationId())
            .orElseThrow(EntityNotFoundException::new);

        return new Section(upStation, downStation, request.getDistance());
    }

    private Optional<Section> getContainsSection(List<Section> sections, Section newSection) {
        return sections.stream()
            .filter(section -> section.isContainSection(newSection))
            .findFirst();
    }

    public void removeSectionByStationId(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
            .orElseThrow(EntityNotFoundException::new);
        Station removeStation = stationRepository.findById(stationId)
            .orElseThrow(EntityNotFoundException::new);
        line.removeSectionByStation(removeStation);
    }
}
