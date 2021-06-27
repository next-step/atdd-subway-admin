package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;
    private final StationService stationService;

    public SectionService(SectionRepository sectionRepository, StationService stationService) {
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
    }

    public Section saveSectionWith(Line line, LineRequest request) {
        Station upStation = getUpStation(request);
        Station downStation = getDownStation(request);
        int distance = getDistance(request);

        return sectionRepository.save(
            new Section(line, upStation, downStation, distance)
        );
    }

    private Station getUpStation(LineRequest request) {
        return stationService.getStation(request.getUpStationId());
    }

    private Station getDownStation(LineRequest request) {
        return stationService.getStation(request.getDownStationId());
    }

    private int getDistance(LineRequest request) {
        return request.getDistance();
    }
}
