package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Embeddable
public class Sections {
  @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
  private List<Section> sections = new ArrayList<>();

  public Sections() {
  }

  public Sections(List<Section> sections) {
    this.sections = sections;
  }

  public Sections of(List<Section> sections) {
    return new Sections(sections);
  }

  public void add(Section section) {
    sections.add(section);
  }

  private Section upEndPoint() {
    return sections.stream()
      .filter(this::isUpEndPoint)
      .findAny()
      .orElseThrow(IllegalStateException::new);
  }

  private Station downEndPointStation() {
    return sections.stream()
      .filter(this::isDownEndPoint)
      .findAny()
      .orElseThrow(IllegalStateException::new)
      .getDownStation();
  }

  private boolean isDownEndPoint(Section parentSection) {
    return sections.stream()
      .map(Section::getUpStation)
      .noneMatch(station -> station.equals(parentSection.getDownStation()));
  }

  private boolean isUpEndPoint(Section parentSection) {
    return sections.stream()
      .map(Section::getDownStation)
      .noneMatch(station -> station.equals(parentSection.getUpStation()));
  }

  public Stations getUpToDownStations() {
    List<Station> stations = new ArrayList<>(Arrays.asList(upEndPoint().getUpStation(), upEndPoint().getDownStation()));
    while (!isEndPointStation(stations)) {
      stations.add(findNextLinkedStation(stations));
    }

    return new Stations(stations);
  }

  private Station findNextLinkedStation(List<Station> stations) {
    return sections.stream()
      .filter(section -> section.getUpStation().equals(stationsLastElement(stations)))
      .findAny()
      .orElseThrow(IllegalArgumentException::new)
      .getDownStation();
  }

  private boolean isEndPointStation(List<Station> stations) {
    return stationsLastElement(stations).equals(downEndPointStation());
  }

  private Station stationsLastElement(List<Station> stations) {
    return stations.get(stations.size() - 1);
  }
}
