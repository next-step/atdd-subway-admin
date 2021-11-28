package nextstep.subway.sections.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {
  @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
  private List<Section> sections = new ArrayList<>();

  public Sections() {}

  public Sections(List<Section> sections) {
    this.sections = sections;
  }

  public Sections of(List<Section> sections) {
    return new Sections(sections);
  }

  public void add(Section section) {
    sections.add(section);
  }

  public Section upEndPoint() {
    return sections.stream()
      .filter(this::isUpEndPoint)
      .findAny()
      .orElseThrow(IllegalStateException::new);
  }

  private boolean isUpEndPoint(Section parentSection) {
    return sections.stream()
      .map(Section::getDownStation)
      .noneMatch(station -> station.equals(parentSection.getUpStation()));
  }

  public Stations getUpToDownStations() {
    List<Station> stations = new ArrayList<>();
    Section upEndPointSection = upEndPoint();
    stations.add(upEndPointSection.getUpStation());
    Station nextStation = upEndPointSection.getDownStation();
    while (true) {
      stations.add(nextStation);
      final Station finalNextStation = nextStation;
      Section nextSection = sections.stream()
        .filter(section -> section.getUpStation().equals(finalNextStation))
        .findAny().orElse(null);
      if (nextSection == null) break;
      nextStation = nextSection.getDownStation();
    }
    return new Stations(stations);
  }
}
