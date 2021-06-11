package nextstep.subway.station.application;

import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

public interface StationCommandUseCase {
    StationResponse saveStation(StationRequest stationRequest);

    void deleteStationById(Long id);
}
