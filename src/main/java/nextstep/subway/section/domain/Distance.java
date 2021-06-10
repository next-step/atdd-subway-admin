package nextstep.subway.section.domain;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {

  private static final int MIN_DISTANCE = 1;
  private static final String DISTANCE_SHOULD_BE_LARGER_THAN_ZERO = "역 간 거리는 %d 이상이어야 합니다.";

  private int number;

  protected Distance() {}

  private Distance(int number) {
    this.number = number;
  }

  public static Distance from(int distance) {
    validateDistanceMinimumValue(distance);
    return new Distance(distance);
  }

  private static void validateDistanceMinimumValue(int distance) {
    if(distance < MIN_DISTANCE) {
      throw new IllegalArgumentException(String.format(DISTANCE_SHOULD_BE_LARGER_THAN_ZERO, MIN_DISTANCE));
    }
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public Distance subtract(Distance other) {
    return from(this.number - other.number);
  }

  public Distance add(Distance other) {
    return from(this.number + other.number);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Distance distance = (Distance) o;
    return number == distance.number;
  }

  @Override
  public int hashCode() {
    return Objects.hash(number);
  }
}
