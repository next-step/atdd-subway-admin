package nextstep.subway.line.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Distance distance1 = (Distance) o;
    return distance == distance1.distance;
  }

  @Override
  public int hashCode() {
    return Objects.hash(distance);
  }
}
