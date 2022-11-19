package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import nextstep.subway.domain.Line;
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
    private final LineService lineService;
    private final SectionRepository sectionRepository;

    public SectionService(StationService stationService, LineService lineService,
                          SectionRepository sectionRepository) {
        this.stationService = stationService;
        this.lineService = lineService;
        this.sectionRepository = sectionRepository;
    }

    public List<SectionResponse> findResponsesByLineId(Long lineId) {
        List<Section> sections = sectionRepository.findByLineId(lineId)
                .orElseThrow(EntityNotFoundException::new);

        return sections.stream()
                .map(this::getSectionResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineService.findEntityWithSectionsById(lineId);
        Station upStation = stationService.findEntityById(sectionRequest.getUpStationId());
        Station downStation = stationService.findEntityById(sectionRequest.getDownStationId());
        int distance = sectionRequest.getDistance();

        line.validateAlreadyAndNotExistsStations(upStation, downStation);
        isFindSameUpStationThenCreateNewSection(line.getSections(), upStation, downStation, distance);
        isFindSameDownStationThenCreateNewSection(line.getSections(), upStation, downStation, distance);
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
