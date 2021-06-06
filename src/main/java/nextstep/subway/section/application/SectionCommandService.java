package nextstep.subway.section.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    private final StationQueryService stationQueryService;
    private final SectionQueryService sectionQueryService;
    private final SectionRepository sectionRepository;

    public SectionCommandService(StationQueryService stationQueryService,
                                 SectionQueryService sectionQueryService,
                                 SectionRepository sectionRepository) {
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

        List<Section> newSections = new ArrayList<>();

        for (Section section : sections.getSections()) {

            if (section.equalsUpStation(downStation)) {
                newSections.add(new Section(upStation, downStation, distance));
                newSections.add(section.updateUpStation(downStation, distance));
                continue;
            }

            if (section.equalsUpStation(upStation)) {
                newSections.add(new Section(upStation, downStation, section.minusDistance(distance)));
                newSections.add(section.updateUpStation(downStation, distance));
                continue;
            }

            if (section.equalsDownStation(upStation)) {
                newSections.add(section.updateDownStation(upStation, distance));
                newSections.add(new Section(upStation, downStation, distance));
                continue;
            }

            if (section.equalsDownStation(downStation)) {
                newSections.add(section.updateDownStation(upStation, distance));
                newSections.add(new Section(upStation, downStation, section.minusDistance(distance)));
                continue;
            }

            if (section.hasNotUpAndDownStation(upStation, downStation)) {
                newSections.add(section);
            }
        }

        newSections.forEach(sectionRepository::save);
        return new LineSections(newSections);
    }
}
