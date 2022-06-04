package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StationTest {

    @Test
    @DisplayName("역이름은 필수이다.")
    void createStation() {
        assertThatIllegalArgumentException().isThrownBy(() -> Station.createStation(null));
    }

    @Test
    @DisplayName("역이름이 같으면 같다.")
    void sameStation() {
        assertAll(
            () -> assertThat(Station.createStation("노원역")).isEqualTo(Station.createStation("노원역")),
            () -> assertThat(Station.createStation("노원역")).isNotEqualTo(Station.createStation("주안역"))
        );
    }
}
