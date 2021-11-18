package nextstep.subway.station.domain;

import nextstep.subway.station.dto.StationResponse;

import java.util.*;
import java.util.stream.Collectors;

/**
 * packageName : nextstep.subway.station.domain
 * fileName : Stations
 * author : haedoang
 * date : 2021-11-18
 * description :
 */
public class Stations {
    private final List<Station> stations;

    private Stations(List<Station> stationList) {
        this.stations = new ArrayList<>(validate(stationList));
    }

    public static Stations of(List<Station> stationList) {
        return new Stations(stationList);
    }

    private List<Station> validate(List<Station> stationList) {
        return Optional.ofNullable(stationList).orElseGet(Collections::emptyList)
                .stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<StationResponse> toDto() {
        return this.stations.stream().map(StationResponse::of).collect(Collectors.toList());
    }
}
