package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.exception.SectionException;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Section extends BaseEntity {

  public static final String WRONG_DISTANCE = "역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JoinColumn(name = "lineId")
  @ManyToOne(fetch = FetchType.LAZY)
  private Line line;

  @JoinColumn(name = "upStationId")
  @ManyToOne
  private Station upStation;

  @JoinColumn(name = "downStationId")
  @ManyToOne
  private Station downStation;

  private int distance;

  public Section(Line line, Station upStation, Station downStation, int distance) {
    this.line = line;
    this.upStation = upStation;
    this.downStation = downStation;
    this.distance = distance;
  }

  public void changeLine(final Line line) {
    this.line = line;
  }

  public boolean isDownStationInSection(Station station) {
    return this.downStation.equals(station);
  }

  public boolean isUpStationInSection(Station upStation) {
    if (this.upStation == null) {
      return false;
    }
    return this.upStation.getId().equals(upStation.getId());
  }

  public void updateUpToDown(Station preStation, int distance) {
    validateDistance(distance);
    this.upStation = preStation;
    this.distance -= distance;
  }

  public void updateDownToUp(Station station, int distance) {
    validateDistance(distance);
    this.downStation = station;
    this.distance -= distance;
  }

  private void validateDistance(int distance) {
    if (this.upStation != null && this.distance <= distance) {
      throw new SectionException(WRONG_DISTANCE);
    }
  }

}
