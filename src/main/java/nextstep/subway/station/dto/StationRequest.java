package nextstep.subway.station.dto;

import nextstep.subway.station.domain.Station;

public class StationRequest {
    private String name;

    /* RequestBody로 사용되는 클래스의 경우에는 Serialization을 위해 
    * No-argument Constructor가 꼭 필요하다! 
    * 없는 경우에는 400 에러 발생*/
    public StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Station toStation() {
        return new Station(name);
    }
}
