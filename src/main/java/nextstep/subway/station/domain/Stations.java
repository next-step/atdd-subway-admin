package nextstep.subway.station.domain;

import nextstep.subway.station.exception.StationDuplicateException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Stations {
  private static final long VALID_INPUT_MATCH_COUNT = 1;
  private List<Station> stations = new ArrayList<>();

  protected Stations() {}

  public Stations(List<Station> stations) {
    checkDuplicateStationExists(stations);
    this.stations = stations;
  }

  private void checkDuplicateStationExists(List<Station> stations) {
    for (Station station : stations) {
      duplicateStationValidate(stations, station);
    }
  }

  private void duplicateStationValidate(List<Station> stations, Station newStation) {
    long matchCount = stations.stream()
      .filter(station -> station.equals(newStation))
      .count();

    if (matchCount > VALID_INPUT_MATCH_COUNT) {
      throw new StationDuplicateException(newStation.getName());
    }
  }

  public void addStation(Station station) {
    stations.add(station);
  }

  public List<Station> getStations() {
    return stations;
  }

  public void setStations(List<Station> stations) {
    this.stations = stations;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Stations stations1 = (Stations) o;
    return Objects.equals(stations, stations1.stations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stations);
  }

  @Override
  public String toString() {
    return "Stations{" +
      "stations=" + stations +
      '}';
  }

  public List<Station> asList() {
    return stations;
  }
}
