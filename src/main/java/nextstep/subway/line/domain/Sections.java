package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

  @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST,
      CascadeType.MERGE}, orphanRemoval = true)
  private final List<Section> sections = new ArrayList<>();

  public Sections(Line line, Station upStation, Station downStation, int distance) {
    this.sections.add(new Section(line, upStation, downStation, distance));
  }

  protected Sections() {

  }


  public List<Section> getStations() {
    return sections;
  }

  public void add(Section section) {
    this.sections.add(section);
  }
}
