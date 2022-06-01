package nextstep.subway.domain;

import nextstep.subway.exception.CustomException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class LineStationsTest {

    private Station firstStation;
    private Station secondStation;
    private Station thirdStation;

    @BeforeEach
    void setUp() {
        firstStation = new Station(1L, "첫번째 지하철역");
        secondStation = new Station(2L, "두번째 지하철역");
        thirdStation = new Station(3L, "세번째 지하철역");
    }

    @Test
    void 첫번째_구간을_추가할_수_있다() {
        LineStation lineStation = new LineStation(11L, 10L, firstStation, secondStation);
        LineStations lineStations = new LineStations();

        lineStations.add(lineStation);

        assertThat(lineStations.size()).isEqualTo(1);
        assertThat(lineStations.getSortedStations()).containsExactly(firstStation, secondStation);
    }

    @Test
    void 중간_구간을_추가할_수_있다() {
        LineStation firstLineStation = new LineStation(11L, 10L, firstStation, secondStation);
        LineStation secondLineStation = new LineStation(12L, 5L, firstStation, thirdStation);
        LineStations lineStations = new LineStations();

        lineStations.add(firstLineStation);
        lineStations.add(secondLineStation);

        assertThat(lineStations.size()).isEqualTo(2);
        assertThat(lineStations.getSortedStations()).containsExactly(firstStation, thirdStation, secondStation);
    }

    @ParameterizedTest
    @ValueSource(longs = {10, 11})
    void 중간_구간을_추가할_때_거리가_동일하거나_크면_추가할_수_없다(Long distance) {
        LineStation firstLineStation = new LineStation(11L, 10L, firstStation, secondStation);
        LineStation secondLineStation = new LineStation(12L, distance, firstStation, thirdStation);
        LineStations lineStations = new LineStations();

        lineStations.add(firstLineStation);
        Assertions.assertThatExceptionOfType(CustomException.class)
                .isThrownBy(() -> lineStations.add(secondLineStation));
    }

    @Test
    void 상행_종점_구간을_추가할_수_있다() {
        LineStation firstLineStation = new LineStation(11L, 10L, firstStation, secondStation);
        LineStation secondLineStation = new LineStation(12L, 5L, thirdStation, firstStation);
        LineStations lineStations = new LineStations();

        lineStations.add(firstLineStation);
        lineStations.add(secondLineStation);

        assertThat(lineStations.size()).isEqualTo(2);
        assertThat(lineStations.getSortedStations()).containsExactly(thirdStation, firstStation, secondStation);
    }

    @Test
    void 하행_종점_구간을_추가할_수_있다() {
        LineStation firstLineStation = new LineStation(11L, 10L, firstStation, secondStation);
        LineStation secondLineStation = new LineStation(12L, 5L, secondStation, thirdStation);
        LineStations lineStations = new LineStations();

        lineStations.add(firstLineStation);
        lineStations.add(secondLineStation);

        assertThat(lineStations.size()).isEqualTo(2);
        assertThat(lineStations.getSortedStations()).containsExactly(firstStation, secondStation, thirdStation);
    }

    @Test
    void 상하행선이_동일하면_추가할_수_없다() {
        LineStation firstLineStation = new LineStation(11L, 10L, firstStation, secondStation);
        LineStation secondLineStation = new LineStation(12L, 5L, firstStation, secondStation);
        LineStations lineStations = new LineStations();

        lineStations.add(firstLineStation);
        Assertions.assertThatExceptionOfType(CustomException.class)
                .isThrownBy(() -> lineStations.add(secondLineStation));
    }

    @Test
    void 상하행선_모두_일치하지_않으면_추가할_수_없다() {
        Station fourthStation = new Station(4L, "네번째 지하철역");

        LineStation firstLineStation = new LineStation(11L, 10L, firstStation, secondStation);
        LineStation secondLineStation = new LineStation(12L, 5L, thirdStation, fourthStation);
        LineStations lineStations = new LineStations();

        lineStations.add(firstLineStation);
        Assertions.assertThatExceptionOfType(CustomException.class)
                .isThrownBy(() -> lineStations.add(secondLineStation));
    }

}