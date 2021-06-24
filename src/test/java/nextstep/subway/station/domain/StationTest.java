package nextstep.subway.station.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StationTest {

    @DisplayName("이름이 빈 값이면 생성을 실패한다")
    @Test
    void create_EmptyName_ExceptionThrown() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            new Station(null)
        );
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            new Station("")
        );
    }

}
