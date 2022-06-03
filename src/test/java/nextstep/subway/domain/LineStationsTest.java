package nextstep.subway.domain;

import nextstep.subway.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class LineStationsTest {

    private Station firstStation;
    private Station secondStation;
    private Station thirdStation;
    private LineStations lineStations;

    @BeforeEach
    void setUp() {
        firstStation = new Station(1L, "첫번째 지하철역");
        secondStation = new Station(2L, "두번째 지하철역");
        thirdStation = new Station(3L, "세번째 지하철역");
        lineStations = new LineStations();
    }

    @Test
    void 첫번째_구간을_추가할_수_있다() {
        LineStation lineStation = new LineStation(11L, 10L, firstStation, secondStation);

        lineStations.add(lineStation);

        assertThat(lineStations.size()).isEqualTo(1);
        assertThat(lineStations.getSortedStations()).containsExactly(firstStation, secondStation);
    }

    @Test
    void 중간_구간을_추가할_수_있다() {
        LineStation firstLineStation = new LineStation(11L, 10L, firstStation, secondStation);
        LineStation secondLineStation = new LineStation(12L, 5L, firstStation, thirdStation);

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

        lineStations.add(firstLineStation);
        assertThatExceptionOfType(CustomException.class)
                .isThrownBy(() -> lineStations.add(secondLineStation));
    }

    @Test
    void 상행_종점_구간을_추가할_수_있다() {
        LineStation firstLineStation = new LineStation(11L, 10L, firstStation, secondStation);
        LineStation secondLineStation = new LineStation(12L, 5L, thirdStation, firstStation);

        lineStations.add(firstLineStation);
        lineStations.add(secondLineStation);

        assertThat(lineStations.size()).isEqualTo(2);
        assertThat(lineStations.getSortedStations()).containsExactly(thirdStation, firstStation, secondStation);
    }

    @Test
    void 하행_종점_구간을_추가할_수_있다() {
        LineStation firstLineStation = new LineStation(11L, 10L, firstStation, secondStation);
        LineStation secondLineStation = new LineStation(12L, 5L, secondStation, thirdStation);

        lineStations.add(firstLineStation);
        lineStations.add(secondLineStation);

        assertThat(lineStations.size()).isEqualTo(2);
        assertThat(lineStations.getSortedStations()).containsExactly(firstStation, secondStation, thirdStation);
    }

    @Test
    void 상하행선이_동일하면_추가할_수_없다() {
        LineStation firstLineStation = new LineStation(11L, 10L, firstStation, secondStation);
        LineStation secondLineStation = new LineStation(12L, 5L, firstStation, secondStation);

        lineStations.add(firstLineStation);
        assertThatExceptionOfType(CustomException.class)
                .isThrownBy(() -> lineStations.add(secondLineStation));
    }

    @Test
    void 상하행선_모두_일치하지_않으면_추가할_수_없다() {
        Station fourthStation = new Station(4L, "네번째 지하철역");

        LineStation firstLineStation = new LineStation(11L, 10L, firstStation, secondStation);
        LineStation secondLineStation = new LineStation(12L, 5L, thirdStation, fourthStation);

        lineStations.add(firstLineStation);
        assertThatExceptionOfType(CustomException.class)
                .isThrownBy(() -> lineStations.add(secondLineStation));
    }

    @ParameterizedTest
    @CsvSource(value = {"1,2,3", "2,1,3", "3,1,2"})
    void 역을_삭제할_수_있다(Long deleteStationId, Long firstStationId, Long secondStationId) {
        lineStations.add(new LineStation(11L, 10L, firstStation, secondStation));
        lineStations.add(new LineStation(12L, 5L, secondStation, thirdStation));

        Map<Long, Station> stationById = convertToMapById();

        lineStations.delete(stationById.get(deleteStationId));

        assertThat(lineStations.size()).isEqualTo(1);
        assertThat(lineStations.getSortedStations()).containsExactly(stationById.get(firstStationId), stationById.get(secondStationId));
    }

    private Map<Long, Station> convertToMapById() {
        return Stream.of(firstStation, secondStation, thirdStation)
                .collect(Collectors.toMap(Station::getId, Function.identity()));
    }

    @Test
    void 등록되지_않은_역은_삭제할_수_없다() {
        lineStations.add(new LineStation(11L, 10L, firstStation, secondStation));
        lineStations.add(new LineStation(12L, 5L, secondStation, thirdStation));

        Station fourthStation = new Station(4L, "네번째 지하철역");

        assertThatExceptionOfType(CustomException.class)
                .isThrownBy(() -> lineStations.delete(fourthStation));
    }

    @Test
    void 구간이_하나인_노선에서_마지막_구간을_제거할_수_없다() {
        lineStations.add(new LineStation(11L, 10L, firstStation, secondStation));

        assertThatExceptionOfType(CustomException.class)
                .isThrownBy(() -> lineStations.delete(firstStation));
    }
}