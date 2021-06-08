package nextstep.subway.section.application;

import java.util.Optional;
import nextstep.subway.line.application.LineQueryService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.LineSections;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.application.StationQueryService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionCommandService {

    private final LineQueryService lineQueryService;
    private final StationQueryService stationQueryService;
    private final SectionQueryService sectionQueryService;

    private final SectionRepository sectionRepository;

    public SectionCommandService(LineQueryService lineQueryService,
                                 StationQueryService stationQueryService,
                                 SectionQueryService sectionQueryService,
                                 SectionRepository sectionRepository) {
        this.lineQueryService = lineQueryService;
        this.stationQueryService = stationQueryService;
        this.sectionQueryService = sectionQueryService;
        this.sectionRepository = sectionRepository;
    }

    public Long save(Long upStationId, Long downStationId, int distance) {

        Optional<Section> maybeSection =
            sectionQueryService.findByUpStationAndDownStation(upStationId, downStationId);

        if (maybeSection.isPresent()) {
            return maybeSection.get().getId();
        }

        Section entity = new Section(stationQueryService.findById(upStationId),
                                     stationQueryService.findById(downStationId),
                                     distance);

        return sectionRepository.save(entity).getId();
    }

    public LineSections upsert(LineSections sections,
                               Long upStationId,
                               Long downStationId,
                               int distance) {

        Station upStation = stationQueryService.findById(upStationId);
        Station downStation = stationQueryService.findById(downStationId);

        sections.verifyStationCycle(upStation, downStation);
        sections.verifyNotUpdatable(upStation, downStation);

        Optional<Section> updatedSection = sections.updateSection(upStation, downStation, distance);
        updatedSection.ifPresent(sectionRepository::save);

        Section newSection = sectionRepository.save(new Section(upStation, downStation, new Distance(distance)));
        sections.add(newSection);

        return sections;
    }

    public void delete(Long lineId, Long stationId) {
        Line line = lineQueryService.findById(lineId);
        LineSections lineSections = line.getSections();
        lineSections.deleteSection(stationQueryService.findById(stationId));
    }
}
