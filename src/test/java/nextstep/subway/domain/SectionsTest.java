package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SectionsTest {

    private static Sections sections;

    @BeforeEach
    void setUp() {
        sections = new Sections();
        Section 강남역에서_양재역 = new Section(StationTest.강남역, StationTest.양재역, 5);
        sections.addInitialSection(강남역에서_양재역);
    }

    @DisplayName("구간 추가 후 거리 확인")
    @Test
    void getDistanceTest() {
        Section 양재역에서_양재시민의숲역 = new Section(StationTest.양재역, StationTest.양재시민의숲역, 10);
        sections.add(양재역에서_양재시민의숲역);
        assertEquals(15L, sections.getDistance());
    }

    @DisplayName("구간 추가 후 순서 확인")
    @Test
    void getSectionListTest() {
        Section 양재역에서_양재시민의숲역 = new Section(StationTest.양재역, StationTest.양재시민의숲역, 10);
        sections.add(양재역에서_양재시민의숲역);

        Section 신논현역에서_강남역 = new Section(StationTest.신논현역, StationTest.강남역, 5);
        sections.add(신논현역에서_강남역);

        List<String> stationNames = getStationNames();

        assertThat(stationNames).hasSize(4)
                .containsExactly("신논현역", "강남역", "양재역", "양재시민의숲역");
    }

    private List<String> getStationNames() {
        List<String> stationNames = sections.getSectionList().stream()
                .map(section -> section.getUpStation().getName())
                .collect(Collectors.toList());
        int lastIndex = sections.getSectionList().size() - 1;
        stationNames.add(sections.getSectionList().get(lastIndex).getDownStation().getName());
        return stationNames;
    }

    @DisplayName("등록된 구간 사이에 새로운 구간 추가")
    @Test
    void addTest() {
        Section 양재시민의숲역에서_양재역 = new Section(StationTest.양재시민의숲역, StationTest.양재역, 2);
        sections.add(양재시민의숲역에서_양재역);

        List<String> stationNames = getStationNames();
        assertThat(stationNames).hasSize(3)
                .containsExactly("강남역", "양재시민의숲역", "양재역");
    }

    @DisplayName("이미 구간이 등록된 상태에서 초기 구간 등록 시도")
    @Test
    void addInitialSectionTest() {
        Section 양재시민의숲역에서_양재역 = new Section(StationTest.양재시민의숲역, StationTest.양재역, 2);
        assertThrows(IllegalStateException.class, () -> sections.addInitialSection(양재시민의숲역에서_양재역)
                , "이미 구간이 등록된 경우 추가할 수 없습니다.");
    }

    @DisplayName("상행역으로 이미 등록되어 있는지 확인")
    @Test
    void hasSameUpStationTest() {
        assertTrue(sections.hasSameUpStation(StationTest.강남역));
    }

    @DisplayName("종착역으로 등록되어 있는지 확인")
    @Test
    void hasSameDownStationTest() {
        assertTrue(sections.hasSameDownStation(StationTest.양재역));
    }

    @DisplayName("등록된 구간 사이에 있는 역을 삭제")
    @Test
    void removeSectionByStationTest() {
        Section 양재역에서_양재시민의숲역 = new Section(StationTest.양재역, StationTest.양재시민의숲역, 10);
        sections.add(양재역에서_양재시민의숲역);
        sections.removeSectionByStation(StationTest.양재역);

        List<String> stationNames = getStationNames();
        assertThat(stationNames).hasSize(2)
                .containsExactly("강남역", "양재시민의숲역");
    }
}
