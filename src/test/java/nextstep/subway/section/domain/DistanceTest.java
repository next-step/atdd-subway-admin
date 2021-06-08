package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DistanceTest {
    private Station station1;
    private Station station2;
    private Line line;
    private Section section;

    @BeforeEach
    void setUp() {
        station1 = new Station("강남역");
        station2 = new Station("역삼역");
        line = new Line("2호선", "green");
        section = new Section(line, station1, station2, 20);
    }

    @DisplayName("0보다 작은 값으로 거리 생성시 예외를 던진다.")
    @Test
    void createWithZeroDistance() {
        assertThatThrownBy(() -> new Distance(0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void minus() {
        // given
        Distance givenDistance = new Distance(5);

        Distance actual = givenDistance.minus(new Distance(3));

        assertThat(actual).isEqualTo(new Distance(2));
    }
}
