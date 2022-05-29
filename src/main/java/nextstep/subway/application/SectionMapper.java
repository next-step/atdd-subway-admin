package nextstep.subway.application;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import org.springframework.stereotype.Component;

@Component
public class SectionMapper {
    private final StationService stationService;

    public SectionMapper(StationService stationService) {
        this.stationService = stationService;
    }

    public Section from(LineRequest lineRequest) {
        Station upStation = stationService.findById(lineRequest.getUpStationId());
        Station downStation = stationService.findById(lineRequest.getDownStationId());

        return new Section(lineRequest.getDistance(), upStation, downStation);
    }
}
