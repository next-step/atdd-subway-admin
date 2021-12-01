package nextstep.subway.line;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

public class TestLineFactory {
  public static Line lineOf(Long id, String name, String color) {
    return new Line(id, name, color);
  }

  public static Station stationOf(Long id, String name) {
    return new Station(id, name);
  }
}
