package nextstep.subway.application;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import org.springframework.stereotype.Service;

@Service
public class SectionService {
    private final StationService stationService;

    public SectionService(StationService stationService) {
        this.stationService = stationService;
    }

    public Section generateSection(LineRequest lineRequest) {
        Station upStation = stationService.findStationById(lineRequest.getUpStationId());
        Station downStation = stationService.findStationById(lineRequest.getDownStationId());
        return new Section(upStation, downStation, lineRequest.getDistance());
    }
}
