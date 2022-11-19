package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {
    private Line 역2개_2호선;
    private Line 역3개_2호선;
    private Station 서초역;
    private Station 강남역;
    private Station 교대역;

    @BeforeEach
    void setUp() {
        서초역 = Station.from("서초역");
        교대역 = Station.from("교대역");
        강남역 = Station.from("강남역");
        역2개_2호선 = Line.builder().upStation(서초역).downStation(강남역).distance(10).build();
        역3개_2호선 = Line.builder().upStation(서초역).downStation(강남역).distance(10).build();
        역3개_2호선.addSection(Section.from(서초역, 교대역, Distance.from(5)));
    }

    @Test
    @DisplayName("노선에 할당된 역을 조회할 수 있다.")
    void findAssignedStations_success() {
        assertThat(역2개_2호선.findAssignedStations()).containsExactly(서초역, 강남역);
    }

    @Test
    @DisplayName("구간을 추가하고 노선 조회 시 추가한 역 조회를 성공한다.")
    void addSections_success() {
        Section newSection = Section.from(서초역, 교대역, Distance.from(5));
        역2개_2호선.addSection(newSection);
        assertThat(역2개_2호선.findAssignedStations()).containsExactly(서초역, 교대역, 강남역);
    }

    @Test
    @DisplayName("구간을 제거하고 노선 조회 시 제거한 역이 조회되지 않는다.")
    void deleteSections_success() {
        역3개_2호선.deleteSection(교대역);
        assertThat(역3개_2호선.findAssignedStations()).containsExactly(서초역, 강남역);
    }
}
