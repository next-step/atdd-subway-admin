package nextstep.subway.domain;

import nextstep.subway.dto.StationToLineResponse;

import java.util.List;
import java.util.stream.Collectors;

public class Stations {

    private final List<Station> stationList;

    public Stations(List<Station> stationList) {
        this.stationList = stationList;
    }

    public Station findById(long stationId) {
        return stationList.stream().filter(station -> station.isSameId(stationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("요청하신 지하철역을 찾을 수 없습니다. 요청id값:"+ stationId));
    }

    public List<StationToLineResponse> makeStationToLineResponse() {
        return stationList.stream()
                .map(StationToLineResponse::fromStation)
                .collect(Collectors.toList());
    }
}
