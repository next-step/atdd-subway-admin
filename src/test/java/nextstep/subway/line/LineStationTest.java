package nextstep.subway.line;

import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class LineStationTest {

    private Station 강남역;
    private Station 양재역;
    private Station 수원역;
    private Station 인천역;
    private Line 신분당선;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        수원역 = new Station("수원역");
        인천역 = new Station("인천역");
        신분당선 = new Line("신분당선", "bg-red-600");
    }

    @DisplayName("line, station, preStation, distance 를 가진다.")
    @Test
    void createTest() {
        assertThat(new LineStation(신분당선, new Section(강남역, 양재역, new Distance(10L))))
                .isEqualTo(new LineStation(신분당선, new Section(강남역, 양재역, new Distance(10L))));
    }

    @DisplayName("전역을 알려줄수 있다.")
    @Test
    void getPreStationTest() {
        assertThat((new LineStation(신분당선, new Section(강남역, 양재역, new Distance(10L))).getPreStation()))
                .isEqualTo(강남역);
    }

    @DisplayName("출발역인지 알수 있다.")
    @ParameterizedTest
    @MethodSource("providePreStationAndExpectedResult")
    void isStartStationTest(final Station preStation, final boolean expectedResult) {
        final LineStation createLineStation = new LineStation(신분당선, new Section(preStation, 양재역, new Distance(10L)));
        assertThat(createLineStation.isStartStation()).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> providePreStationAndExpectedResult() {
        return Stream.of(
                Arguments.of(new Station("강남역"), false),
                Arguments.of(null, true)
        );
    }

    @DisplayName("현재노선을 알수 있다.")
    @Test
    void getLineTest() {
        assertThat((new LineStation(신분당선, new Section(강남역, 양재역, new Distance(10L))).getLine()))
                .isEqualTo(신분당선);
    }

    @DisplayName("입력값을 통해서 현재 역인지 확인할수 있다.")
    @Test
    void getCurrentStationTest() {
        assertThat((new LineStation(신분당선, new Section(강남역, 양재역, new Distance(10L))).isCurrentStation(양재역))).isTrue();
    }

    @DisplayName("전역과의 거리를 알수 있다.")
    @Test
    void getDistanceTest() {
        assertThat(new LineStation(신분당선, 강남역).getDistance()).isEqualTo(new Distance(0L));
    }

    @DisplayName("같은 노선인 경우에만 역을 추가할수 있다.")
    @Test
    void addLineStationTest() {
        final LineStation startStation = new LineStation(신분당선, 강남역);
        LineStation 신분당선_양재역 = new LineStation(신분당선, new Section(강남역, 양재역, 3L));
        assertThat(startStation.addLineStation(신분당선_양재역)).isEqualTo(신분당선_양재역);

        Station 신논현역 = new Station("신논현역");
        LineStation 신분당선_신논현역 = new LineStation(신분당선, new Section(강남역,신논현역, 2L));
        LineStation updatedLineStation = 신분당선_양재역.addLineStation(신분당선_신논현역);
        assertThat(updatedLineStation).isEqualTo(신분당선_신논현역);
        assertThat(신분당선_양재역).isEqualTo(new LineStation(신분당선, new Section(신논현역,양재역, 1L)));
    }

    @DisplayName("같은 노선인 아닐 경우 역을 추가하면 에러를 발생한다.")
    @Test
    void invalidAddLineStationTest() {
        LineStation addLineStation = new LineStation(new Line("1호선", "bg-blue-600"), new Section(강남역, 수원역, 10L));
        assertThatThrownBy(() -> new LineStation(신분당선, 강남역).addLineStation(addLineStation))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
