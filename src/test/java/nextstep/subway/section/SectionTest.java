package nextstep.subway.section;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionTest {

    private Section 신사_교대_구간;
    private Station 신사역;
    private Station 교대역;
    private Line 삼호선;

    @BeforeEach
    public void setUp() {
        삼호선 = new Line("삼호선", "주황");
        신사역 = new Station(1L, "신사역");
        교대역 = new Station(2L, "교대역");

        신사_교대_구간 = new Section(삼호선, 신사역, 교대역, 100, true);
    }

    @DisplayName("상행을 기준으로 새로운 하행 생성")
    @Test
    public void test1() {
        Station 잠원역 = new Station(3L, "잠원역");
        Section 신사_잠원_구간 = 신사_교대_구간.createAndChange(신사역, 잠원역, 50);
        assertThat(신사_잠원_구간.getDistance()).isEqualTo(50);
        assertThat(신사_교대_구간.getDistance()).isEqualTo(50);
        assertThat(신사_잠원_구간.getUp()).isEqualTo(신사역);
        assertThat(신사_잠원_구간.getDown()).isEqualTo(잠원역);
        assertThat(신사_교대_구간.getUp()).isEqualTo(잠원역);
        assertThat(신사_교대_구간.getDown()).isEqualTo(교대역);
    }

    @DisplayName("하행을 기준으로 새로운 상행 생성")
    @Test
    public void test2() {
        Station 잠원역 = new Station(3L, "잠원역");
        Section 잠원_교대_구간 = 신사_교대_구간.createAndChange(잠원역, 교대역, 50);
        assertThat(잠원_교대_구간.getDistance()).isEqualTo(50);
        assertThat(신사_교대_구간.getDistance()).isEqualTo(50);
        assertThat(잠원_교대_구간.getUp()).isEqualTo(잠원역);
        assertThat(잠원_교대_구간.getDown()).isEqualTo(교대역);
        assertThat(신사_교대_구간.getUp()).isEqualTo(신사역);
        assertThat(신사_교대_구간.getDown()).isEqualTo(잠원역);
    }

    @DisplayName("잘못된 역")
    @Test
    public void test3() {
        Station 강남역 = new Station(3L, "강남역");
        Station 정자역 = new Station(4L, "정자역");
        Section section = 신사_교대_구간.createAndChange(강남역, 정자역, 50);
        assertThat(section).isNull();;
    }

}
