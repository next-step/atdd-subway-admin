package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

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

  public Section(final Line line, final Station upStation, final Station downStation, final int distance) {
    this.line = line;
    this.upStation = upStation;
    this.downStation = downStation;
    this.distance = distance;
  }

  public void addLine(final Line line) {
    this.line = line;
  }

  public List<Station> getStations() {
    return Arrays.asList(upStation, downStation);
  }

}
