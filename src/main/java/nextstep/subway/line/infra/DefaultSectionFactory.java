package nextstep.subway.line.infra;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.SectionFactory;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.exception.StationNotFoundException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DefaultSectionFactory implements SectionFactory {

    private final StationRepository stationRepository;

    @Override
    public Section create(final Long upStationId, final Long downStationId, final int distance) {
        Station upStation = getStation(upStationId);
        Station downStation = getStation(downStationId);
        return Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(Distance.valueOf(distance))
                .build();
    }

    private Station getStation(final Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new StationNotFoundException(String.format("역이 존재하지 않습니다. (입력 id값: %d)", stationId)));
    }
}
