package nextstep.subway.line;

import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class LineStationsTest {
    private Line 신분당선;
    private Station 강남역;
    private Station 논현역;
    private Station 양재역;
    private LineStations 신분당선_역정보들;

    @BeforeEach
    void setUp() {
        신분당선 = new Line("신분당선", "bg-red-600");
        강남역 = new Station("강남역");
        논현역 = new Station("논현역");
        양재역 = new Station("양재역");
        신분당선_역정보들 = new LineStations(Collections.singletonList(new LineStation(신분당선, 강남역)));
    }

    @DisplayName("LineStation List 를 입력받는다.")
    @Test
    void createTest() {
        assertThat(신분당선_역정보들).isEqualTo(new LineStations(Collections.singletonList(new LineStation(신분당선, 강남역))));
    }

    @DisplayName("LineStation List 를 입력시 null 를 입력할수 없다")
    @Test
    void invalidCreateTest() {
        assertThatThrownBy(() -> new LineStations(null)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("LineStation 을 추가 할수 있다.")
    @Test
    void addLineStationTest() {
        LineStation lineStation = new LineStation(신분당선, new Section(강남역, 양재역, 10L));
        신분당선_역정보들.addLineStation(lineStation);
        assertThat(신분당선_역정보들.isContains(lineStation)).isTrue();
    }

    @DisplayName("이미 등록된 LineStation 을 추가시 에러가 발생한다.")
    @Test
    void invalidAddLineStationTest() {
        신분당선_역정보들.addLineStation(new LineStation(신분당선, new Section(강남역, 양재역, 10L)));
        신분당선_역정보들.addLineStation(new LineStation(신분당선, new Section(양재역, 논현역, 10L)));

        assertThatThrownBy(() -> 신분당선_역정보들.addLineStation(new LineStation(신분당선, new Section(new Station("강남역"), new Station("논현역"), 1L))))
                .isExactlyInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> 신분당선_역정보들.addLineStation(new LineStation(신분당선, new Section(new Station("인천역"), new Station("수원역"), 1L))))
                .isExactlyInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> 신분당선_역정보들.addLineStation(new LineStation(신분당선, new Section(new Station("강남역"), new Station("양재역"), 1L))))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("순서대로 LineStation 정보 가져오기")
    @Test
    void getLineStationBySorted() {
        Line line = new Line("신분당선", "bg-black-800");
        final LineStation 신분당선_신사역 = new LineStation(line, new Station("신사역"));
        final LineStation 신분당선_논현 = new LineStation(line, new Section(new Station("신사역"), new Station("신논현"), 2L));
        final LineStation 신분당선_신논현 = new LineStation(line, new Section(new Station("신사역"), new Station("논현"), 1L));
        final LineStation 신분당선_강남역 = new LineStation(line, new Section(new Station("신논현"), 강남역, 1L));
        final LineStation 신분당선_양재역 = new LineStation(line, new Section(강남역, 양재역, 1L));


        LineStations 신분당선_노선들 = new LineStations(Arrays.asList(신분당선_양재역, 신분당선_강남역, 신분당선_신사역, 신분당선_논현, 신분당선_신논현));
        assertThat(신분당선_노선들.getStation().stream().map(Station::getName).toArray()).containsExactly( "신사역","논현","신논현","강남역","양재역" );
    }
}
