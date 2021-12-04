package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Line extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String name;

  private String color;

  @Embedded
  private final Sections sections = new Sections();

  protected Line() {
  }

  public Line(String name, String color) {
    this.name = name;
    this.color = color;
  }

  public Line(Long id, String name, String color) {
    this.id = id;
    this.name = name;
    this.color = color;
  }

  public static Line of(String name, String color, Station upStation, Station downStation, Distance distance) {
    Line line = new Line(name, color);
    Section section = Section.of(upStation, downStation, distance);
    section.addLine(line);
    line.sections.add(section);
    return line;
  }

  public void addSection(Section section) {
    section.addLine(this);
    sections.add(section);
  }

  public void update(Line line) {
    this.name = line.getName();
    this.color = line.getColor();
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getColor() {
    return color;
  }

  public Stations getStations() {
    return sections.getUpToDownStations();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Line line = (Line) o;
    return Objects.equals(id, line.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
