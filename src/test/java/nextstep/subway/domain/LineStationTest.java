package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineStationTest {
    private Station 상행_종점역;
    private Station 하행_종점역;
    private Line 노선;
    private LineStation 구간;

    @BeforeEach
    void setUp() {
        상행_종점역 = new Station("상행종점역");
        하행_종점역 = new Station("하행종점역");
        노선 = new Line("1호선", "dark-blue");
        구간 = new LineStation(상행_종점역, 하행_종점역, 10, 노선);
    }

    @Test
    @DisplayName("상행 종점 추가 여부를 확인한다.")
    void 상행_종점_추가_여부_확인() {
        LineStation 상행_종점_구간 = new LineStation(null, 상행_종점역, 0, 노선);
        LineStation 추가_가능_구간 = new LineStation(new Station("인천역"), 상행_종점역, 10, 노선);
        LineStation 추가_불가_구간 = new LineStation(new Station("신도림역"), 하행_종점역, 10, 노선);

        assertAll(
                () -> assertThat(상행_종점_구간.canAddFirstLineStation(추가_가능_구간)).isTrue(),
                () -> assertThat(상행_종점_구간.canAddFirstLineStation(추가_불가_구간)).isFalse()
        );

    }

    @Test
    @DisplayName("하행 종점 추가 여부를 확인한다.")
    void 하행_종점_추가_여부_확인() {
        LineStation 하행_종점_구간 = new LineStation(하행_종점역, null, 0, 노선);
        LineStation 추가_가능_구간 = new LineStation(하행_종점역, new Station("소요산역"), 10, 노선);
        LineStation 추가_불가_구간 = new LineStation(new Station("신도림역"), 하행_종점역, 10, 노선);

        assertAll(
                () -> assertThat(하행_종점_구간.canAddLastLineStation(추가_가능_구간)).isTrue(),
                () -> assertThat(하행_종점_구간.canAddLastLineStation(추가_불가_구간)).isFalse()
        );
    }

    @Test
    @DisplayName("중간 구간 추가 여부를 확인한다.")
    void 구간_추가_여부_확인() {
        LineStation 추가_가능_구간 = new LineStation(상행_종점역, new Station("구로역"), 5, 노선);
        LineStation 추가_불가_구간 = new LineStation(new Station("인천역"), new Station("구로역"), 5, 노선);

        assertAll(
                () -> assertThat(구간.canAddInterLineStation(추가_가능_구간)).isTrue(),
                () -> assertThat(구간.canAddInterLineStation(추가_불가_구간)).isFalse()
        );
    }

    @Test
    @DisplayName("동일한 구간인지 확인한다.")
    void 동일구간_여부_확인() {
        LineStation 동일_구간 = new LineStation(상행_종점역, 하행_종점역, 10, 노선);
        LineStation 다른_구간 = new LineStation(new Station("인천역"), new Station("구로역"), 5, 노선);

        assertAll(
                () -> assertThat(구간.isSameLineStation(동일_구간)).isTrue(),
                () -> assertThat(구간.isSameLineStation(다른_구간)).isFalse()
        );
    }

    @Test
    @DisplayName("구간 길이를 비교한다.")
    void 구간_길이_비교() {
        LineStation 긴_구간 = new LineStation(상행_종점역, 하행_종점역, 300, 노선);
        LineStation 같은_구간 = new LineStation(상행_종점역, 하행_종점역, 10, 노선);
        LineStation 짧은_구간 = new LineStation(상행_종점역, 하행_종점역, 5, 노선);

        assertAll(
                () -> assertThat(구간.isShorterThan(긴_구간)).isTrue(),
                () -> assertThat(구간.isShorterThan(같은_구간)).isTrue(),
                () -> assertThat(구간.isShorterThan(짧은_구간)).isFalse()
        );
    }
}
