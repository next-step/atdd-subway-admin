package nextstep.subway.line.infra;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.domain.LastStation;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineFactory;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;

@Component
@RequiredArgsConstructor
public class DefaultLineFactory implements LineFactory {

    private final StationRepository stationRepository;

    @Override
    public Line create(final LineRequest lineRequest) {
        Station upStation = getStation(lineRequest.getUpStationId());
        Station downStation = getStation(lineRequest.getDownStationId());
        return Line.of(
                lineRequest.getName(),
                lineRequest.getColor(),
                lineRequest.getDistance(),
                new LastStation(upStation, downStation)
        );
    }

    private Station getStation(final Long upStationId) {
        return stationRepository.findById(upStationId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("역이 존재하지 않습니다. (입력 id값: %d", upStationId)));
    }
}
