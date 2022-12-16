package nextstep.subway.application;

import nextstep.subway.domain.Station;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;
import org.springframework.stereotype.Component;

@Component
class StationMapper {

    Station mapToDomainEntity(StationRequest stationRequest) {
        return new Station(stationRequest.getName());
    }

    StationResponse mapToResponse(Station station) {
        return new StationResponse(
            station.getId(),
            station.getName(),
            station.getCreatedDate(),
            station.getModifiedDate());
    }
}
