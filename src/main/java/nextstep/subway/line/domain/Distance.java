package nextstep.subway.line.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
  private static final int MINIMUM_DISTANCE = 1;
  @Column
  private int distance;

  protected Distance() {}

  public Distance(int distance) {
    checkDistanceValidate(distance);
    this.distance = distance;
  }

  private void checkDistanceValidate(int distance) {
    if (distance < MINIMUM_DISTANCE) {
      throw new IllegalArgumentException("거리의 최소 값은 " + MINIMUM_DISTANCE +  " 입니다. 입력: " + distance);
    }
  }

  public static Distance of(int distance) {
    return new Distance(distance);
  }

  public int getDistance() {
    return distance;
  }
}
