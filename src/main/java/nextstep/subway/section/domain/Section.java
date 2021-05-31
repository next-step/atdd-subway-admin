package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "up_station_id")
  @Column(nullable = false)
  private Station upStation;
  @ManyToOne
  @JoinColumn(name = "down_station_id")
  @Column(nullable = false)
  private Station downStation;
  @Column(nullable = false)
  private Integer distance;
  @ManyToOne
  @JoinColumn(name = "line_id")
  @Column(nullable = false)
  private Line line;

  protected Section() {}

  public Section(Station upStation, Station downStation, int distance) {
    this.upStation = upStation;
    this.downStation = downStation;
    this.distance = distance;
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

  public Integer getDistance() {
    return distance;
  }

  public Line getLine() {
    return line;
  }

  public void addLine(Line line) {
    this.line = line;
  }
}
