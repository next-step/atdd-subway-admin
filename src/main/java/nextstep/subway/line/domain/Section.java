package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
  private Line line;

  @ManyToOne(optional = false)
  @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_up_station"))
  private Station upStation;

  @ManyToOne(optional = false)
  @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_down_station"))
  private Station downStation;

  @Embedded
  private Distance distance;

  protected Section() {}

  public Section(Long id, Line line, Station upStation, Station downStation, Distance distance) {
    this.id = id;
    this.line = line;
    this.upStation = upStation;
    this.downStation = downStation;
    this.distance = distance;
  }

  public static Section of(Station upStation, Station downStation, Distance distance) {
    return new Section(null, null, upStation, downStation, distance);
  }

  public void updateUpSideSection(Section newSection) {
    upStation = newSection.downStation;
    distance = distance.minus(newSection.distance);
  }

  public void updateDownSideSection(Section newSection) {
    downStation = newSection.upStation;
    distance = distance.minus(newSection.distance);
  }

  public void updateDownStation(Station downStation) {
    this.downStation = downStation;
  }

  public boolean isMatch(Section section) {
    return upStation.equals(section.upStation) && downStation.equals(section.downStation);
  }

  public void addDistance(Distance distance) {
    this.distance.add(distance);
  }

  public void addLine(Line line) {
    this.line = line;
  }

  public Long getId() {
    return id;
  }

  public Station getUpStation() {
    return upStation;
  }

  public Station getDownStation() {
    return downStation;
  }

  public Distance getDistance() {
    return distance;
  }

  public Line getLine() {
    return line;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Section section = (Section) o;
    return Objects.equals(id, section.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "Section{" +
      "id=" + id +
      ", line=" + line +
      ", upStation=" + upStation +
      ", downStation=" + downStation +
      ", distance=" + distance +
      '}';
  }
}
