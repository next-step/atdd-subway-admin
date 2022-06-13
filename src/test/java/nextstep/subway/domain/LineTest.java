package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LineTest {

    private static Section 강남역에서_양재역;
    private static Line 신분당선;

    @BeforeEach
    void setUp() {
        long distance = 5;
        강남역에서_양재역 = new Section(StationTest.강남역, StationTest.양재역, distance);
        신분당선 = new Line("신분당선", "bg-red-001", 강남역에서_양재역);
    }

    @DisplayName("노선 신규 추가 후 구간 조회")
    @Test
    void addInitialSectionTest() {
        assertEquals(신분당선.getSections().get(0), 강남역에서_양재역);
    }

    @DisplayName("이름 및 색상 변경")
    @Test
    void updateTest() {
        신분당선.update("다른분당선", "bg-red-002");
        assertEquals("다른분당선", 신분당선.getName());
        assertEquals("bg-red-002", 신분당선.getColor());
    }

    @DisplayName("종착역에 신규 구간 추가")
    @Test
    void addLastSectionTest() {
        Section 양재역에서_양재시민의숲역 = new Section(StationTest.양재역, StationTest.양재시민의숲역, 10);
        신분당선.addSection(양재역에서_양재시민의숲역);
        assertEquals(StationTest.양재시민의숲역, 신분당선.getDownStation());
    }

    @DisplayName("출발역에 신규 구간 추가")
    @Test
    void addFirstSectionTest() {
        Section 신논현역에서_강남역 = new Section(StationTest.신논현역, StationTest.강남역, 10);
        신분당선.addSection(신논현역에서_강남역);
        assertEquals(StationTest.신논현역, 신분당선.getUpStation());
    }

    @DisplayName("신규 구간 추가 후 거리 확인")
    @Test
    void getDistanceTest() {
        Section 신논현역에서_강남역 = new Section(StationTest.신논현역, StationTest.강남역, 10);
        신분당선.addSection(신논현역에서_강남역);
        assertEquals(15L, 신분당선.getDistance());
    }

    @DisplayName("구간이 하나만 남은 경우 삭제 불가")
    @Test
    void removeLastSectionByStation() {
        assertThrows(IllegalStateException.class, () -> 신분당선.removeSectionByStation(StationTest.강남역)
                , "마지막 구간은 제거할 수 없습니다.");
    }

    @DisplayName("종착역을 추가한 후 삭제하는 경우 종착역 변경")
    @Test
    void removeLastStation() {
        Section 양재역에서_양재시민의숲역 = new Section(StationTest.양재역, StationTest.양재시민의숲역, 10);
        신분당선.addSection(양재역에서_양재시민의숲역);
        신분당선.removeSectionByStation(StationTest.양재시민의숲역);
        assertEquals(StationTest.양재역, 신분당선.getDownStation());
    }

    @DisplayName("출발역을 추가한 후 삭제하는 경우 출발역 변경")
    @Test
    void removeFirstStation() {
        Section 신논현역에서_강남역 = new Section(StationTest.신논현역, StationTest.강남역, 10);
        신분당선.addSection(신논현역에서_강남역);
        신분당선.removeSectionByStation(StationTest.신논현역);
        assertEquals(StationTest.강남역, 신분당선.getUpStation());
    }
}
