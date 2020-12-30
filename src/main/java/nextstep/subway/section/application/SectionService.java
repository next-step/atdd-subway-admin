package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionService {

    private StationService stationService;

    public SectionService(StationService stationService) {
        this.stationService = stationService;
    }

    public Section createSection(Line line, Long upStationId, Long downStationId, int distance) {
        Station upStation = stationService.selectStationById(upStationId);
        Station downStation = stationService.selectStationById(downStationId);

        Section section = Section.builder()
                .line(line)
                .upStation(upStation)
                .downStation(downStation)
                .distance(new Distance(distance))
                .build();

        return section;
    }
}
