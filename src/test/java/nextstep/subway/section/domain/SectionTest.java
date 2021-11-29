package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

class SectionTest {
    private Station 강남역;
    private Station 판교역;
    private Station 양재역;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        판교역 = new Station("판교역");
    }

    @DisplayName("전달받은 역이 하행인지 확인한다")
    @Test
    void hasDownStation() {
        Section section = new Section(강남역, 판교역, new Distance(10));

        assertThat(section.hasDownStation(판교역)).isTrue();
        assertThat(section.hasDownStation(강남역)).isFalse();
    }

    @DisplayName("전달받은 역이 상행인지 확인한다")
    @Test
    void hasUpStation() {
        Section section = new Section(강남역, 판교역, new Distance(10));

        assertThat(section.hasUpStation(강남역)).isTrue();
        assertThat(section.hasUpStation(판교역)).isFalse();
    }

    @DisplayName("구간 추가로 인한 하행 업데이트")
    @Test
    void updateDown() {
        Section section = new Section(강남역, 판교역, new Distance(10));
        Section added = new Section(양재역, 판교역, new Distance(2));

        section.updateDown(added);

        assertThat(section).isEqualTo(new Section(강남역, 양재역, new Distance(8)));
    }

    @DisplayName("구간 추가로 인한 상행 업데이트")
    @Test
    void updateUp() {
        Section section = new Section(강남역, 판교역, new Distance(10));
        Section added = new Section(강남역, 양재역, new Distance(2));

        section.updateUp(added);

        assertThat(section).isEqualTo(new Section(양재역, 판교역, new Distance(8)));
    }
}