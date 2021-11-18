package nextstep.subway.station.dto;

import nextstep.subway.station.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class StationResponseList {

    private List<StationResponse> stationResponseList;

    public StationResponseList() {
    }

    public StationResponseList(final List<Station> list) {
        this.stationResponseList = list.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public List<StationResponse> getStationResponseList() {
        return stationResponseList;
    }

    public int size() {
        return stationResponseList.size();
    }

}
