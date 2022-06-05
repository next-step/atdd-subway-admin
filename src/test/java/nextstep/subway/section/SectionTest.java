package nextstep.subway.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("구간에 대한 단위 테스트")
class SectionTest {

    @DisplayName("기존 구간에 하행 구간이 추가되면 기존의 downStation 이 "
        + "target 의 upStation 으로 변경되고 distance 가 감소되어야 한다")
    @Test
    void add_test() {
        // given
        Station 기존_구간_upStation = new Station(1L, "테스트");
        Station 기존_구간_downStation = new Station(2L, "테스트2");
        Section 기존_구간 = Section.of(기존_구간_upStation, 기존_구간_downStation, 10L);

        Station 추가될_구간_upStation = new Station(1L, "테스트");
        Station 추가될_구간_downStation = new Station(3L, "테스트3");
        Section 추가될_구간 = Section.of(추가될_구간_upStation, 추가될_구간_downStation, 7L);

        // when
        Section 추가된_구간 = 기존_구간.addBetweenSection(추가될_구간);

        // then
        assertAll(
            () -> assertThat(기존_구간.getUpStation()).isEqualTo(추가될_구간.getDownStation()),
            () -> assertThat(기존_구간.getDistanceValue()).isEqualTo(3L),
            () -> assertThat(추가된_구간.getDownStation()).isEqualTo(기존_구간.getUpStation()),
            () -> assertThat(추가된_구간.getDistanceValue()).isEqualTo(7L)
        );
    }


    @DisplayName("기존 구간에 상행 구간이 추가되면 기존의 upStation 이 "
        + "target 의 downStation 으로 변경되고 distance 가 감소되어야 한다")
    @Test
    void add_upStream_endPoint_test() {
        // given
        Station 기존_구간_upStation = new Station(1L, "테스트");
        Station 기존_구간_downStation = new Station(2L, "테스트2");
        Section 기존_구간 = Section.of(기존_구간_upStation, 기존_구간_downStation, 10L);

        Station 추가될_구간_upStation = new Station(5L, "테스트2");
        Station 추가될_구간_downStation = new Station(2L, "테스트2");
        Section 추가될_구간 = Section.of(추가될_구간_upStation, 추가될_구간_downStation, 7L);

        // when
        Section 추가된_구간 = 기존_구간.addBetweenSection(추가될_구간);

        // then
        assertAll(
            () -> assertThat(기존_구간.getDownStation()).isEqualTo(추가될_구간.getUpStation()),
            () -> assertThat(기존_구간.getDistanceValue()).isEqualTo(3L),
            () -> assertThat(추가된_구간.getUpStation()).isEqualTo(기존_구간.getDownStation()),
            () -> assertThat(추가된_구간.getDistanceValue()).isEqualTo(7L)
        );
    }

    @DisplayName("구간에 삭제될 구간을 재배치 하면 기존 구간의 downStation 이 "
        + "target 의 downStation 으로 변경되고 두 구간의 distance 가 합쳐져야 한다")
    @Test
    void relocate_test() {
        // given
        Station 구간1_upStation = new Station(1L, "테스트");
        Station 구간1_downStation = new Station(2L, "테스트2");
        Section 구간1 = Section.of(구간1_upStation, 구간1_downStation, 10L);

        Station 구간2_upStation = new Station(2L, "테스트2");
        Station 구간2_downStation = new Station(3L, "테스트3");
        Section 구간2 = Section.of(구간2_upStation, 구간2_downStation, 7L);

        // when
        구간1.relocate(구간2);

        // then
        assertAll(
            () -> assertThat(구간1.getDownStation()).isEqualTo(구간2.getDownStation()),
            () -> assertThat(구간1.getDistanceValue()).isEqualTo(17)
        );
    }
}
