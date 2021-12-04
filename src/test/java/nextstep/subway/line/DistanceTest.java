package nextstep.subway.line;

import nextstep.subway.line.domain.Distance;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DistanceTest {
  @DisplayName("길이는 0이하가 될 수 없다.")
  @Test
  void 길이_양수가_아닐_경우_예외() {
    assertThatThrownBy(() -> new Distance(0))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("거리의 최소 값은 1 입니다. 입력: 0");
  }

  @DisplayName("길이를 원하는 수만큼 뺀다.")
  @Test
  void 길이_빼기() {
    // given
    Distance distance = new Distance(10);

    // when
    Distance minusDistance = distance.minus(Distance.of(4));

    assertThat(minusDistance).isEqualTo(Distance.of(6));
  }

  @DisplayName("길이를 뺄 때 0이하의 값이 나오면 예외를 던진다.")
  @Test
  void 길이_빼기_양수가_아닐_경우_예외() {
    // given
    Distance distance = new Distance(10);

    assertThatThrownBy(() -> distance.minus(Distance.of(11)))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("거리의 최소 값은 1 입니다. 입력: -1");
  }
}
