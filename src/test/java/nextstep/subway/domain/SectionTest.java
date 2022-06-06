package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionTest {
    Section section;
    Station station1;
    Station station2;
    Station station3;
    Station station4;

    Line line;

    @BeforeEach
    void setUp() {
        station1 = new Station(1L, "강남역");
        station2 = new Station(2L, "력삼역");
        station3 = new Station(3L, "선릉역");
        station4 = new Station(4L, "삼성역");
        line = new Line(1L, "2호선", "bg-100", station1, station3, 100);
        section = new Section(1L, line, station1, station2, 50);
    }

    @Test
    @DisplayName("상행역을 업데이트한다")
    void updateUpStation() {
        section.updateUpStationAndDistance(station3, 40);
        assertThat(section.getDistance()).isEqualTo(10);
        assertThat(section.getUpStation()).isEqualTo(station3);
    }

    @Test
    @DisplayName("새로운 노선의 거리가 더 길면 예외가 발생한다")
    void updateUpStationException() {
        assertThatThrownBy(() -> section.updateUpStationAndDistance(station3, 100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("역 사이의 거리가 초과되었습니다.");
    }

    @Test
    @DisplayName("하행역을 업데이트한다")
    void updateDownStation() {
        section.updateDownStationAndDistance(station3, 40);
        assertThat(section.getDistance()).isEqualTo(10);
        assertThat(section.getDownStation()).isEqualTo(station3);
    }

    @Test
    @DisplayName("새로운 노선의 길이가 더 길면 예외가 발생한다")
    void updateDownStationException() {
        assertThatThrownBy(() -> section.updateDownStationAndDistance(station3, 100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("역 사이의 거리가 초과되었습니다.");
    }

    @Test
    @DisplayName("상행역과 하행역이 같으면 예외가 발생한다")
    void sameUpAndDownStationException() {
        assertThatThrownBy(() -> section.updateUpStationAndDistance(station1, 100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역은 같을 수 없습니다.");

        assertThatThrownBy(() -> new Section(line, station1, station1, 100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역은 같을 수 없습니다.");
    }
}
