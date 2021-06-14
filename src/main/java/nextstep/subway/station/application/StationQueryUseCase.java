package nextstep.subway.station.application;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import java.util.List;

public interface StationQueryUseCase {
    List<StationResponse> findAllStations();

    Station findById(Long id);

    List<Station> findAllByIds(List<Long> ids);
}