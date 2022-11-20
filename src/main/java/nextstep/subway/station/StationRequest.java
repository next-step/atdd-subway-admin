package nextstep.subway.station;

public class StationRequest {

    private String name;

    public String getName() {
        return name;
    }

    public Station toStation() {
        return new Station(name);
    }
}
