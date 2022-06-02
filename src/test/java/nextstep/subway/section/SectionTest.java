package nextstep.subway.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("구간에 대한 단위 테스트")
class SectionTest {

    @DisplayName("기존 노선에 하행 구간이 추가되면 기존의 downStation 이 "
        + "target 의 upStation 으로 변경되고 distance 가 감소되어야 한다")
    @Test
    void add_test() {
        // given
        Station 기존_노선_upStation = new Station(1L, "테스트");
        Station 기존_노선_downStation = new Station(2L, "테스트2");
        Section 기존_노선 = Section.of(기존_노선_upStation, 기존_노선_downStation, 10L);

        Station 추가될_노선_upStation = new Station(1L, "테스트2");
        Station 추가될_노선_downStation = new Station(3L, "테스트3");
        Section 추가될_노선 = Section.of(추가될_노선_upStation, 추가될_노선_downStation, 7L);

        // when
        Section 추가된_노선 = 기존_노선.addBetweenSection(추가될_노선);

        // then
        assertAll(
            () -> assertThat(기존_노선.getUpStation()).isEqualTo(추가될_노선.getDownStation()),
            () -> assertThat(기존_노선.getDistanceValue()).isEqualTo(3L),
            () -> assertThat(추가된_노선.getDownStation()).isEqualTo(기존_노선.getUpStation()),
            () -> assertThat(추가된_노선.getDistanceValue()).isEqualTo(7L)
        );
    }


    @DisplayName("기존 노선에 상행 구간이 추가되면 기존의 upStation 이 "
        + "target 의 downStation 으로 변경되고 distance 가 감소되어야 한다")
    @Test
    void add_test2() {
        // given
        Station 기존_노선_upStation = new Station(1L, "테스트");
        Station 기존_노선_downStation = new Station(2L, "테스트2");
        Section 기존_노선 = Section.of(기존_노선_upStation, 기존_노선_downStation, 10L);

        Station 추가될_노선_upStation = new Station(5L, "테스트2");
        Station 추가될_노선_downStation = new Station(2L, "테스트3");
        Section 추가될_노선 = Section.of(추가될_노선_upStation, 추가될_노선_downStation, 7L);

        // when
        Section 추가된_노선 = 기존_노선.addBetweenSection(추가될_노선);

        // then
        assertAll(
            () -> assertThat(기존_노선.getDownStation()).isEqualTo(추가될_노선.getUpStation()),
            () -> assertThat(기존_노선.getDistanceValue()).isEqualTo(3L),
            () -> assertThat(추가된_노선.getUpStation()).isEqualTo(기존_노선.getDownStation()),
            () -> assertThat(추가된_노선.getDistanceValue()).isEqualTo(7L)
        );
    }
}
