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

    public LineSections upsert(List<Long> sectionIds,
                               Long upStationId,
                               Long downStationId,
                               int distance) {

        List<Section> sections = sectionRepository.findAllById(sectionIds);
        List<Section> newSections = new ArrayList<>();

        Station upStation = stationQueryService.findById(upStationId);
        Station downStation = stationQueryService.findById(downStationId);

        verify(sections, upStation, downStation);

        boolean isUpdate = false;

        for (Section section : sections) {

            if (section.equalsUpStation(downStation)) {
                newSections.add(new Section(upStation, downStation, distance));
                newSections.add(section.updateUpStation(downStation, distance));
                isUpdate = true;
                continue;
            }

            if (section.equalsUpStation(upStation)) {
                newSections.add(new Section(upStation, downStation, section.minusDistance(distance)));
                newSections.add(section.updateUpStation(downStation, distance));
                isUpdate = true;
                continue;
            }

            if (section.equalsDownStation(upStation)) {
                newSections.add(section.updateDownStation(upStation, distance));
                newSections.add(new Section(upStation, downStation, distance));
                isUpdate = true;
                continue;
            }

            if (section.equalsDownStation(downStation)) {
                newSections.add(section.updateDownStation(upStation, distance));
                newSections.add(new Section(upStation, downStation, section.minusDistance(distance)));
                isUpdate = true;
                continue;
            }

            if (section.hasNotUpAndDownStation(upStation, downStation)) {
                newSections.add(section);
            }
        }

        if (!isUpdate) {
            throw new IllegalArgumentException("신규 구간의 상행역과 하행역 모두가 기존 구간에 포함되어 있지 않습니다.");
        }

        newSections.forEach(sectionRepository::save);
        return new LineSections(newSections);
    }

    private void verify(List<Section> sections, Station upStation, Station downStation) {

        if (sections.isEmpty()) {
            return;
        }

        if (sections.size() == 1) {
            Section section = sections.get(0);
            if (section.hasStation(upStation) && section.hasStation(downStation)) {
                throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
            }
        }

        if (sections.stream()
                    .filter(section -> section.hasStation(upStation) || section.hasStation(downStation))
                    .count() >= 2) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }
    }
}
