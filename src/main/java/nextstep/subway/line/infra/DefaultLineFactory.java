package nextstep.subway.line.infra;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineFactory;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.exception.StationNotFoundException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DefaultLineFactory implements LineFactory {

    private final StationRepository stationRepository;

    @Override
    public Line create(final LineRequest lineRequest) {
        Station upStation = getStation(lineRequest.getUpStationId());
        Station downStation = getStation(lineRequest.getDownStationId());
        Distance distance = Distance.valueOf(lineRequest.getDistance());
        Section section = Section.of(upStation, downStation, distance);
        Line line = Line.of(lineRequest.getName(), lineRequest.getColor());
        line.add(section);
        return line;
    }

    private Station getStation(final Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new StationNotFoundException(String.format("역이 존재하지 않습니다. (입력 id값: %d)", stationId)));
    }
}
