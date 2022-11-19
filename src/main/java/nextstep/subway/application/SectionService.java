package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.repository.SectionRepository;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private final StationService stationService;
    private final SectionRepository sectionRepository;

    public SectionService(StationService stationService, SectionRepository sectionRepository) {
        this.stationService = stationService;
        this.sectionRepository = sectionRepository;
    }

    public List<SectionResponse> findSectionsByLineId(Long lineId) {
        List<Section> sections = sectionRepository.findByLineId(lineId)
                .orElseThrow(EntityNotFoundException::new);

        return sections.stream()
                .map(this::getSectionResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Sections sections = createSections(lineId);
        Station upStation = stationService.findStationById(sectionRequest.getUpStationId());
        Station downStation = stationService.findStationById(sectionRequest.getDownStationId());
        int distance = sectionRequest.getDistance();

        validateStations(sections, upStation, downStation);
        isFindSameUpStationThenCreateNewSection(sections, upStation, downStation, distance);
        isFindSameDownStationThenCreateNewSection(sections, upStation, downStation, distance);
    }

    private Sections createSections(Long lineId) {
        Sections sections = new Sections();
        sections.addAll(sectionRepository.findByLineId(lineId).orElseThrow(EntityNotFoundException::new));
        return sections;
    }

    private void validateStations(Sections sections, Station upStation, Station downStation) {
        sections.validateAlreadyExistsStation(upStation, downStation);
        sections.validateNotExistsStation(upStation, downStation);
    }

    private void isFindSameUpStationThenCreateNewSection(Sections sections, Station upStation,
                                                         Station downStation, int distance) {
        addBetweenByUpStation(sections, upStation, downStation, distance);
        prependUpStation(sections, upStation, downStation, distance);
    }

    private void prependUpStation(Sections sections, Station upStation, Station downStation, int distance) {
        sections.findSameUpStation(downStation).ifPresent(section -> sectionRepository
                .save(section.createNewSection(distance, upStation, downStation)));
    }

    private void addBetweenByUpStation(Sections sections, Station upStation, Station downStation,
                                       int distance) {
        sections.findSameUpStation(upStation).ifPresent(section -> {
            section.validateLength(distance);
            sectionRepository.save(section.createNewSection(distance, upStation, downStation));
            sectionRepository.save(section.createNewDownSection(distance, downStation));
            sectionRepository.delete(section);
        });
    }

    private void isFindSameDownStationThenCreateNewSection(Sections sections, Station upStation,
                                                           Station downStation, int distance) {
        addBetweenByDownStation(sections, upStation, downStation, distance);
        appendDownStation(sections, upStation, downStation, distance);
    }

    private void appendDownStation(Sections sections, Station upStation, Station downStation, int distance) {
        sections.findSameDownStation(upStation).ifPresent(section -> sectionRepository
                .save(section.createNewSection(distance, upStation, downStation)));
    }

    private void addBetweenByDownStation(Sections sections, Station upStation, Station downStation,
                                         int distance) {
        sections.findSameDownStation(downStation).ifPresent(section -> {
            section.validateLength(distance);
            sectionRepository.save(section.createNewSection(distance, upStation, downStation));
            sectionRepository.save(section.createNewUpSection(distance, upStation));
            sectionRepository.delete(section);
        });
    }

    private SectionResponse getSectionResponse(Section section) {
        return SectionResponse.of(section);
    }
}
