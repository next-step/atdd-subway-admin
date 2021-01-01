package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.exception.SectionException;
import nextstep.subway.line.util.ErrorMessage;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Section extends BaseEntity {

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

  public boolean isIncludeInSection(Station station) {
    return this.downStation.equals(station);
  }

  public boolean isUpStationInSection(Station preStation) {
    if (this.upStation == null || preStation == null) {
      return this.upStation == preStation;
    }
    return this.upStation.getId().equals(preStation.getId());
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
      throw new SectionException(ErrorMessage.WRONG_DISTANCE);
    }
  }

}
