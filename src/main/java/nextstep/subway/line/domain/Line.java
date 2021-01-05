package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Line extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(unique = true)
  private String name;
  private String color;

  @Embedded
  private Sections sections;

  public Line(String name, String color) {
    this.name = name;
    this.color = color;
  }

  public Line(String name, String color, Station upStation, Station downStation, int distance) {
    this.name = name;
    this.color = color;

    Section upSection = new Section(this, null, upStation, 0);
    Section downSection = new Section(this, upStation, downStation, distance);
    this.sections = new Sections(upSection, downSection);
  }

  public void addSection(final Section section) {
    this.sections.addSection(section);
  }

  public void update(Line line) {
    this.name = line.getName();
    this.color = line.getColor();
  }

  public List<Station> getStations() {
    return this.sections.getStations();
  }

  public void removeSection(Long stationId) {
    this.sections.removeSection(stationId);
  }
}
