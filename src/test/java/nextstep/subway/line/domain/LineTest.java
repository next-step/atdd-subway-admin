package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LineTest {
    @DisplayName("구간 추가")
    @Test
    void addSection() {
        // given
        Station 강남역 = new Station("강남역");
        Station 잠실역 = new Station("잠실역");
        Station 역삼역 = new Station("역삼역");
        Line line = new Line("2호선", "green", 강남역, 잠실역, 10);

        // when
        line.addSection(잠실역, 역삼역, 10);

        // then
        assertThat(line.getStations()).containsExactlyElementsOf(Arrays.asList(강남역, 잠실역, 역삼역));
    }

    @DisplayName("구간 추가 - 기존 구간의 상행역과 신규 구간의 상행역이 같은 경우")
    @Test
    void addSection2() {
        // given
        Station 강남역 = new Station("강남역");
        Station 잠실역 = new Station("잠실역");
        Station 역삼역 = new Station("역삼역");
        Line line = new Line("2호선", "green", 강남역, 잠실역, 10);

        // when
        line.addSection(강남역, 역삼역, 5);

        // then
        assertThat(line.getStations()).containsExactlyElementsOf(Arrays.asList(강남역, 역삼역, 잠실역));
    }

    @DisplayName("구간 추가 - 기존 구간의 하행역과 신규 구간의 하행역이 같은 경우")
    @Test
    void addSection3() {
        // given
        Station 강남역 = new Station("강남역");
        Station 잠실역 = new Station("잠실역");
        Station 역삼역 = new Station("역삼역");
        Line line = new Line("2호선", "green", 강남역, 잠실역, 10);

        // when
        line.addSection(역삼역, 잠실역, 5);

        // then
        assertThat(line.getStations()).containsExactlyElementsOf(Arrays.asList(강남역, 역삼역, 잠실역));
    }

    @DisplayName("구간 추가 - 기존 구간의 상행역과 신규 구간의 하행역이 같은 경우")
    @Test
    void addSection4() {
        // given
        Station 강남역 = new Station("강남역");
        Station 잠실역 = new Station("잠실역");
        Station 역삼역 = new Station("역삼역");
        Line line = new Line("2호선", "green", 강남역, 잠실역, 10);

        // when
        line.addSection(역삼역, 강남역, 5);

        // then
        assertThat(line.getStations()).containsExactlyElementsOf(Arrays.asList(역삼역, 강남역, 잠실역));
    }

    @DisplayName("구간 추가 - 기존 구간의 하행역과 신규 구간의 상행역이 같은 경우")
    @Test
    void addSection5() {
        // given
        Station 강남역 = new Station("강남역");
        Station 잠실역 = new Station("잠실역");
        Station 역삼역 = new Station("역삼역");
        Line line = new Line("2호선", "green", 강남역, 잠실역, 10);

        // when
        line.addSection(잠실역, 역삼역, 5);

        // then
        assertThat(line.getStations()).containsExactlyElementsOf(Arrays.asList(강남역, 잠실역, 역삼역));
    }

    @DisplayName("구간 추가 - 신규 구간의 상행역과 하행역이 해당 노선에 이미 모두 포함되어 있는 경우 예외 발생")
    @Test
    void addSection6() {
        // given
        Station 강남역 = new Station("강남역");
        Station 잠실역 = new Station("잠실역");
        Station 역삼역 = new Station("역삼역");
        Line line = new Line("2호선", "green", 강남역, 잠실역, 10);
        line.addSection(강남역, 역삼역, 5);

        // when
        assertThatThrownBy(() ->{
            line.addSection(강남역, 잠실역, 2);
        }).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("구간 추가 - 신규 구간의 상행역과 하행역이 해당 노선에 이미 모두 포함되어 있지 않은 경우 예외 발생")
    @Test
    void addSection7() {
        // given
        Station 강남역 = new Station("강남역");
        Station 잠실역 = new Station("잠실역");
        Station 역삼역 = new Station("역삼역");
        Station 서울역 = new Station("서울역");
        Line line = new Line("2호선", "green", 강남역, 잠실역, 10);

        // when
        assertThatThrownBy(() ->{
            line.addSection(서울역, 역삼역, 5);
        }).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("구간 추가 - 기존 구간 사이에 신규 구간이 위치할 경우 기존 구간의 거리보다 신규 구간의 거리가 긴 경우")
    @Test
    void addSection8() {
        // given
        Station 강남역 = new Station("강남역");
        Station 잠실역 = new Station("잠실역");
        Station 역삼역 = new Station("역삼역");
        Line line = new Line("2호선", "green", 강남역, 잠실역, 10);

        // when
        assertThatThrownBy(() ->{
            line.addSection(강남역, 역삼역, 12);
        }).isInstanceOf(RuntimeException.class);
    }
}
