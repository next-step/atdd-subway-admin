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
public class Section extends BaseEntity {

  private static final String NEW_SECTION_MUST_SHORTER_THAN_EXIST_SECTION = "새로 등록되는 구간 길이가 기존 역 사이 길이보다 크거나 같을 수 없습니다.";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "up_station_id")
  private Station upStation;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "down_station_id")
  private Station downStation;

  @Column(nullable = false)
  @Embedded
  private Distance distance;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "line_id")
  private Line line;

  protected Section() {}

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

  public boolean isSameEdges(Section other) {
    return this.getUpStation().equals(other.getUpStation()) &&
            this.getDownStation().equals(other.getDownStation());
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

  public boolean isAfter(Section other) {
    return this.getUpStation().equals(other.getDownStation());
  }

  public List<Section> insertNewSection(Section newSection) {
    if (this.isSameUpStation(newSection)) {
      this.updateUpStation(newSection);
      return Stream.of(this, newSection).collect(Collectors.toList());
    }
    if (this.isSameDownStation(newSection)) {
      this.updateDownStation(newSection);
      return Stream.of(this, newSection).collect(Collectors.toList());
    }
    return Stream.of(this, newSection).collect(Collectors.toList());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if(o == null) return false;
    if(!(o instanceof Section)) return false;
    Section section = (Section) o;
    return this.getId().equals(section.getId()) &&
            this.getUpStation().equals(section.getUpStation()) &&
            this.getDownStation().equals(section.getDownStation()) &&
            this.getDistance().equals(section.getDistance()) &&
            this.getLine().equals(section.getLine());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.getId(), this.getUpStation(), this.getDownStation(), this.getDistance(), this.getLine());
  }

  public void updateDownStation(Section newSection) {
    int distanceDiff = distanceDiffWithNewSection(newSection.distance);
    this.downStation = newSection.upStation;
    this.distance = Distance.from(distanceDiff);
  }

  public void updateUpStation(Section newSection) {
    int distanceDiff = distanceDiffWithNewSection(newSection.distance);
    this.upStation = newSection.downStation;
    this.distance = Distance.from(distanceDiff);
  }

  private int distanceDiffWithNewSection(Distance newSectionDistacne) {
    int distanceDiff = this.distance.getNumber() - newSectionDistacne.getNumber();
    if (distanceDiff <= 0) {
      throw new IllegalArgumentException(NEW_SECTION_MUST_SHORTER_THAN_EXIST_SECTION);
    }
    return distanceDiff;
  }
}
