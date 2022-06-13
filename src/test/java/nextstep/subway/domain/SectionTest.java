package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SectionTest {

    private static Section 강남역에서_양재역;

    @BeforeEach
    void setUp() {
        강남역에서_양재역 = new Section(StationTest.강남역, StationTest.양재역, 10);
    }

    @DisplayName("동일한 역을 구간으로 설정")
    @Test
    void hasSameStationTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            Section 강남역에서_강남역 = new Section(StationTest.강남역, StationTest.강남역, 10);
        }, "상행역과 하행역은 동일한 역으로 지정될 수 없습니다.");
    }

    @DisplayName("거리가 음수인 구간 생성")
    @Test
    void createMinusDistanceTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            Section 강남역에서_양재역 = new Section(StationTest.강남역, StationTest.양재역, -1);
        }, "거리는 1 이상의 숫자만 입력이 가능합니다.");
    }

    @DisplayName("구간에 역 포함 여부 확인")
    @Test
    void hasStationTest() {
        assertTrue(강남역에서_양재역.hasStation(StationTest.양재역));
    }

    @DisplayName("다음 구간과 합침")
    @Test
    void mergeWithTest() {
        Section 양재역에서_양재시민의숲역 = new Section(StationTest.양재역, StationTest.양재시민의숲역, 5);
        강남역에서_양재역.mergeWith(양재역에서_양재시민의숲역);
        assertEquals(StationTest.양재시민의숲역, 강남역에서_양재역.getDownStation());
        assertEquals(15, 강남역에서_양재역.getDistance());
    }

    @DisplayName("다음 구간과 합치기가 불가능한 구간")
    @Test
    void mergeWithExceptionTest() {
        Section 양재시민의숲역에서_양재역 = new Section(StationTest.양재시민의숲역, StationTest.양재역, 5);
        assertThrows(IllegalArgumentException.class, () -> 강남역에서_양재역.mergeWith(양재시민의숲역에서_양재역)
                , "구간을 합칠 수 없습니다.");
    }
}
