package nextstep.subway.station;

class StationRequest {
    private String name;

    public StationRequest() { }

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
