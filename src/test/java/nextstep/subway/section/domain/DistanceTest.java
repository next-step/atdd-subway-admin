package nextstep.subway.section.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class DistanceTest {

  @DisplayName("거리는 1이상의 값을 가져야 한다.")
  @Test
  void creationTest() {
    assertAll(
        () -> assertThat(Distance.from(1)).isEqualTo(Distance.from(1)),
        () -> assertThatThrownBy(() -> Distance.from(0)).isInstanceOf(IllegalArgumentException.class),
        () -> assertThatThrownBy(() -> Distance.from(-1)).isInstanceOf(IllegalArgumentException.class)
    );
  }

}
