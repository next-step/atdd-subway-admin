package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public class Section extends BaseEntity implements Comparable<Section> {

  private static final String ONLY_CONNECTED_SECTION_CAN_REMOVE_SHARED_STATION = "연속 된 구간에서 겹치는 역만 제거할 수 있습니다.";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "up_station_id")
  private Station upStation;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "down_station_id")
  private Station downStation;

  @Column(nullable = false)
  @Embedded
  private Distance distance;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "line_id")
  private Line line;

  protected Section() {
  }

  public Section(Station upStation, Station downStation, int distance) {
    this.upStation = upStation;
    this.downStation = downStation;
    this.distance = Distance.from(distance);
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
    return distance.getNumber();
  }

  public Line getLine() {
    return line;
  }

  public void addLine(Line line) {
    this.line = line;
  }

  public List<Station> getUpAndDownStations() {
    return Stream.of(this.getUpStation(), this.getDownStation())
        .collect(Collectors.toList());
  }

  public boolean isSameDownStation(Section other) {
    Station thisDownStation = this.getDownStation();
    Station otherDownStation = other.getDownStation();
    return thisDownStation.equals(otherDownStation);
  }

  public boolean isSameUpStation(Section other) {
    Station thisUpStation = this.getUpStation();
    Station otherUpStation = other.getUpStation();
    return thisUpStation.equals(otherUpStation);
  }

  public void insertNewSection(Section newSection) {
    if (this.isSameUpStation(newSection)) {
      this.updateUpStation(newSection);
    }
    if (this.isSameDownStation(newSection)) {
      this.updateDownStation(newSection);
    }
  }

  public void updateDownStation(Section newSection) {
    this.downStation = newSection.upStation;
    this.distance = this.distance.subtract(newSection.distance);
  }

  public void updateUpStation(Section newSection) {
    this.upStation = newSection.downStation;
    this.distance = this.distance.subtract(newSection.distance);
  }

  public boolean containsStation(Station station) {
    return upStation.equals(station) || downStation.equals(station);
  }

  public boolean containsAsUpStation(Station station) {
    return upStation.equals(station);
  }

  public boolean containsAsDownStation(Station station) {
    return downStation.equals(station);
  }

  public void removeStationBetweenSections(Section other) {
    if(!this.downStation.equals(other.upStation)) {
      throw new IllegalArgumentException(ONLY_CONNECTED_SECTION_CAN_REMOVE_SHARED_STATION);
    }
    this.downStation = other.downStation;
    this.distance = this.distance.add(other.distance);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    if (!(o instanceof Section)) return false;
    Section section = (Section) o;
    return Objects.equals(this.getId(), section.getId()) &&
            Objects.equals(this.getUpStation(), section.getUpStation()) &&
            Objects.equals(this.getDownStation(), section.getDownStation()) &&
            Objects.equals(this.getDistance(), section.getDistance()) &&
            Objects.equals(this.getLine(), section.getLine());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.getId(), this.getUpStation(), this.getDownStation(), this.getDistance(), this.getLine());
  }

  @Override
  public int compareTo(Section o) {
    if (this.downStation.equals(o.upStation)) {
      return -1;
    }

    if (this.equals(o)) {
      return 0;
    }

    return 1;
  }
}
