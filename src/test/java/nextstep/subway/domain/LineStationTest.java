package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineStationTest {
    private final static Station station1 = new Station("삼성역");
    private final static Station station2 = new Station("선릉역");
    private final static Station station3 = new Station("역삼역");
    private final static Station station4 = new Station("강남역");

    @DisplayName("시작 구간에 새로운 시작 구간이 추가되면 새로운 구간이 시작 구간이 되어야 한다")
    @Test
    void addNewFirstLineStation() {
        // given
        LineStation startLineStation = new LineStation(new Distance(10L), station2, station1, true);

        // when
        LineStation newStartLineStation = startLineStation.addStation(station3, station2, new Distance(10L));

        // then
        assertThat(startLineStation.isStartSection()).isFalse();
        assertThat(newStartLineStation.isStartSection()).isTrue();
    }

    @DisplayName("종료 구간에 새로운 시작 구간이 추가되면 새로운 구간이 종료 구간이 되어야 한다")
    @Test
    void addNewLastLineStation() {
        // given
        LineStation lineStation = new LineStation(new Distance(10L), station1, station2, true);

        // when
        LineStation newStartLineStation = lineStation.addStation(station2, station3, new Distance(10L));

        // then
        assertThat(lineStation.isLastSection()).isFalse();
        assertThat(newStartLineStation.isLastSection()).isTrue();
    }

    @DisplayName("기존 구간에 중간 구간을 추가하면 기존 구간의 길이는 추가한 구간만큼 줄어들어야 한다")
    @Test
    void addMiddleLineStation() {
        // given
        LineStation lLineStation = new LineStation(new Distance(10L), station1, station3, true);

        // given
        LineStation newLastLineStation = lLineStation.addStation(station1, station3, new Distance(4L));

        // then
        assertThat(lLineStation.getDistance().getValue()).isEqualTo(6L);
        assertThat(newLastLineStation.getDistance().getValue()).isEqualTo(4L);
    }

    @DisplayName("추가하려는 구간에 추가하고자 하는 지하철 역이 없을 경우 예외가 발생해야 한다")
    @Test
    void addNotContainLineStation() {
        // given
        LineStation lineStation = new LineStation(new Distance(10L), station1, station2, true);

        // then
        assertThatThrownBy(() -> lineStation.addStation(station3, station4, new Distance(10L)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
