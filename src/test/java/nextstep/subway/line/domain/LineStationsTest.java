package nextstep.subway.line.domain;

import nextstep.subway.section.domain.Distance;
import nextstep.subway.station.domain.Station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

public class LineStationsTest {
    private Station 강남역;
    private Station 선릉역;
    private Station 잠실역;
    private Station 왕십리역;

    @BeforeEach
    void createStations() {
        this.강남역 = new Station("강남역") {
            @Override
            public int hashCode() {
                return 1;
            }
        };
        this.선릉역 = new Station("선릉역") {
            @Override
            public int hashCode() {
                return 2;
            }
        };
        this.잠실역 = new Station("잠실역") {
            @Override
            public int hashCode() {
                return 3;
            }
        };
        this.왕십리역 = new Station("왕십리역") {
            @Override
            public int hashCode() {
                return 4;
            }
        };
    }

    @Test
    @DisplayName("노선에 포함된 지하철역 정렬 테스트")
    void sortTest() {
        LineStation 이호선_강남역 = LineStation.createLineStation(강남역, null, 선릉역, new Distance(5)); // 기점
        LineStation 이호선_선릉역 = LineStation.createLineStation(선릉역, 강남역, 잠실역, new Distance(10));
        LineStation 이호선_잠실역 = LineStation.createLineStation(잠실역, 선릉역, 왕십리역, new Distance(15));
        LineStation 이호선_왕십리역 = LineStation.createLineStation(왕십리역, 잠실역, null, new Distance(0)); // 종점

        LineStations 이호선_구간 = new LineStations(asList(이호선_왕십리역, 이호선_잠실역, 이호선_선릉역, 이호선_강남역));

        assertThat(이호선_구간.getSortedStations()).containsExactly(강남역, 선릉역, 잠실역, 왕십리역);
        assertThat(이호선_구간.getSortedStations()).extracting(Station::getName)
                .containsExactly("강남역", "선릉역", "잠실역", "왕십리역");
    }
}
