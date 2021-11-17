package nextstep.subway.station.dto;

import nextstep.subway.station.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class StationsResponse {

    private List<StationResponse> list;

    public StationsResponse() {
    }

    public StationsResponse(final List<Station> list) {
        this.list = list.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public List<StationResponse> getList() {
        return list;
    }

    public int size() {
        return list.size();
    }

}
