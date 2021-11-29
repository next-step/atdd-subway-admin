package nextstep.subway.sections.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
  private Line line;

  @ManyToOne
  @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_up_station"))
  private Station upStation;

  @ManyToOne
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

  public static Section of(Line line, Station upStation, Station downStation, Distance distance) {
    return new Section(null, line, upStation, downStation, distance);
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
