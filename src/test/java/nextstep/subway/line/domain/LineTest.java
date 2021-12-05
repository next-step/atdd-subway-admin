package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("노선 도메인 관련 기능")
class LineTest {
    private Station 강남역;
    private Station 역삼역;
    private Line line;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        line = Line.of("2호선", "green", 강남역, 역삼역, 9);
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // then
        assertAll(
                () -> assertThat(line.getName()).isEqualTo("2호선"),
                () -> assertThat(line.getColor()).isEqualTo("green")
        );
    }

    @DisplayName("노선은 이름과 색깔을 수정할 수 있다.")
    @Test
    void updateLine() {
        // when
        final Line updateLine = Line.of("3호선", "orange", 강남역, 역삼역, 9);
        line.update(updateLine);

        // then
        assertAll(
                () -> assertThat(line.getName()).isEqualTo("3호선"),
                () -> assertThat(line.getColor()).isEqualTo("orange")
        );
    }
}