package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

class LineTest {
    private Station 강남역;
    private Station 양재역;
    private Station 판교역;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        판교역 = new Station("판교역");
    }

    @DisplayName("하행 구간이 겹치는 새로운 구간 추가 업데이트")
    @Test
    void updateSections() {
        Sections sections = Sections.from(Arrays.asList(new Section(강남역, 판교역, new Distance(10))));
        Line line = new Line(1L, "신분당선", "red", sections);

        line.updateSections(new Section(양재역, 판교역, new Distance(5)));

        assertThat(line.getStations()).isEqualTo(Arrays.asList(강남역, 양재역, 판교역));
    }

    @DisplayName("상행 구간이 겹치는 새로운 구간 추가 업데이트")
    @Test
    void updateSections2() {
        Sections sections = Sections.from(Arrays.asList(new Section(강남역, 판교역, new Distance(10))));
        Line line = new Line(1L, "신분당선", "red", sections);

        line.updateSections(new Section(강남역, 양재역, new Distance(5)));

        assertThat(line.getStations()).isEqualTo(Arrays.asList(강남역, 양재역, 판교역));
    }
}