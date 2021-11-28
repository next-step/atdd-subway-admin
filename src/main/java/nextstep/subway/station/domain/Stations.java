package nextstep.subway.station.domain;

import java.util.List;
import java.util.Objects;

public class Stations {
  private List<Station> stations;

  public Stations() {}

  public Stations(List<Station> stations) {
    this.stations = stations;
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
