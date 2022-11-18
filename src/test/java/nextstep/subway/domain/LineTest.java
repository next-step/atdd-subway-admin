package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
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
    void 노선에_할당된_역을_조회할_수_있다() {
        assertThat(역2개_2호선.findAssignedStations()).containsExactly(서초역, 강남역);
    }

    @Test
    void 구간을_추가하고_노선에_할당된_역을_조회시_추가한_역이_조회된다() {
        Section newSection = Section.from(서초역, 교대역, Distance.from(5));
        역2개_2호선.addSection(newSection);
        assertThat(역2개_2호선.findAssignedStations()).containsExactly(서초역, 교대역, 강남역);
    }

    @Test
    void 구간을_제거하고_노선에_할당된_역을_조회시_제거한_역이_조회되지_않는다() {
        역3개_2호선.deleteSection(교대역);
        assertThat(역3개_2호선.findAssignedStations()).containsExactly(서초역, 강남역);
    }
}
