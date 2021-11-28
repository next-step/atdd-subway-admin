package nextstep.subway.station.domain;


public class StationSection {

    private final Station station;

    private final Station nextStation;

    private StationSection(Station station, Station nextStation) {
        this.station = station;
        this.nextStation = nextStation;
    }

    public static StationSection of(Station station, Station nextStation) {
        return new StationSection(station, nextStation);
    }

    public Station getStation() {
        return station;
    }

    public Station getNextStation() {
        return nextStation;
    }

    public Long getStationId() {
        return station.getId();
    }

    public Long getNextStationId() {
        return nextStation.getId();
    }
}
