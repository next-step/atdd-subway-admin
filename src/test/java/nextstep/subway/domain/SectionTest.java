package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionTest {
    Section section;

    @BeforeEach
    void setUp() {
        section = new Section(1L, LineTest.L1, StationTest.S1, StationTest.S2, 50);
    }

    @Test
    @DisplayName("상행역을 업데이트한다")
    void updateUpStation() {
        section.updateUpStation(StationTest.S3, 40);
        assertThat(section.getDistance()).isEqualTo(10);
        assertThat(section.getUpStation()).isEqualTo(StationTest.S3);
    }

    @Test
    @DisplayName("새로운 노선의 거리가 더 길면 예외가 발생한다")
    void updateUpStationException() {
        assertThatThrownBy(() -> section.updateUpStation(StationTest.S3, 100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("역 사이의 거리가 초과되었습니다.");
    }

    @Test
    @DisplayName("하행역을 업데이트한다")
    void updateDownStation() {
        section.updateDownStation(StationTest.S3, 40);
        assertThat(section.getDistance()).isEqualTo(10);
        assertThat(section.getDownStation()).isEqualTo(StationTest.S3);
    }

    @Test
    @DisplayName("새로운 노선의 길이가 더 길면 예외가 발생한다")
    void updateDownStationException() {
        assertThatThrownBy(() -> section.updateDownStation(StationTest.S3, 100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("역 사이의 거리가 초과되었습니다.");
    }

    @Test
    @DisplayName("상행역과 하행역이 같으면 예외가 발생한다")
    void sameUpAndDownStationException() {
        assertThatThrownBy(() -> section.updateUpStation(StationTest.S1, 100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역은 같을 수 없습니다.");

        assertThatThrownBy(() -> new Section(LineTest.L1, StationTest.S1, StationTest.S1, 100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역은 같을 수 없습니다.");
    }
}
