package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    private final static Station 강남역 = new Station(1L, "강남역");
    private final static Station 삼성역 = new Station(2L, "삼성역");
    private final static Station 잠실역 = new Station(3L, "잠실역");
    private final static Station 성수역 = new Station(4L, "성수역");

    @Test
    @DisplayName("특정 구간에 중간역이 추가되는 경우 현재 저장된 구간 정보를 변경한다")
    void changeStationInfo() {
        // given
        final Section 강남역_잠실역 = new Section(1L, 강남역, 잠실역, 10);
        final Section 삼성역_잠실역 = new Section(2L, 삼성역, 잠실역, 5);

        // when
        강남역_잠실역.changeStationInfo(삼성역_잠실역);

        // then
        assertThat(강남역_잠실역.getLineStations()).containsExactly(강남역, 삼성역);
    }

    @Test
    @DisplayName("현재 저장된 구간보다 변경하려는 구간의 길이가 더 작아야 한다")
    void changeStationInfoError() {
        // given
        final Section 강남역_잠실역 = new Section(1L, 강남역, 잠실역, 5);
        final Section 삼성역_잠실역 = new Section(2L, 삼성역, 잠실역, 5);

        // when
        assertThatIllegalArgumentException().isThrownBy(
                () -> 강남역_잠실역.changeStationInfo(삼성역_잠실역)
        );
    }

    @Test
    @DisplayName("현재 구간과 입력한 구간의 중복 역을 제거한 구간을 반환한다")
    void combineStationInfo() {
        // given
        final Section 강남역_삼성역 = new Section(1L, 강남역, 삼성역, 5);
        final Section 삼성역_잠실역 = new Section(2L, 삼성역, 잠실역, 5);

        // when
        Section actual = 강남역_삼성역.combineSections(삼성역_잠실역);

        // then
        assertThat(actual.getLineStations()).containsExactly(강남역, 잠실역);
    }

    @Test
    @DisplayName("해당 구간이 다른 구간의 시작역 또는 종착역에 해당한다")
    void hasAnyStations_downStation() {
        // given
        final Section 강남역_삼성역 = new Section(1L, 강남역, 삼성역, 5);
        final Section 삼성역_잠실역 = new Section(2L, 삼성역, 잠실역, 5);

        // when
        boolean actual = 강남역_삼성역.hasAnyStations(삼성역_잠실역);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("해당 구간이 다른 구간의 시작역 또는 종착역에 해당한다")
    void hasAnyStations_upStation() {
        // given
        final Section 강남역_삼성역 = new Section(1L, 강남역, 삼성역, 5);
        final Section 삼성역_잠실역 = new Section(2L, 삼성역, 잠실역, 5);

        // when
        boolean actual = 삼성역_잠실역.hasAnyStations(강남역_삼성역);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("해당 구간이 다른 구간의 시작역 또는 종착역에 해당하지 않는다")
    void hasAnyStations_no() {
        // given
        final Section 강남역_삼성역 = new Section(1L, 강남역, 삼성역, 5);
        final Section 잠실역_성수역 = new Section(2L, 잠실역, 성수역, 5);

        // when
        boolean actual = 강남역_삼성역.hasAnyStations(잠실역_성수역);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("해당 구간이 다른 구간과 동일하다")
    void hasAllStations_true() {
        // given
        final Section 강남역_삼성역 = new Section(1L, 강남역, 삼성역, 5);

        // when
        boolean actual = 강남역_삼성역.hasAllStations(강남역_삼성역);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("해당 구간이 다른 구간과 완전히 동일하지 않다")
    void hasAllStations_false() {
        // given
        final Section 강남역_삼성역 = new Section(1L, 강남역, 삼성역, 5);
        final Section 잠실역_성수역 = new Section(2L, 잠실역, 성수역, 5);

        // when
        boolean actual = 강남역_삼성역.hasAllStations(잠실역_성수역);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("해당 구간이 다른 구간과 일부 동일하지 않다")
    void hasAllStations_false2() {
        // given
        final Section 강남역_삼성역 = new Section(1L, 강남역, 삼성역, 5);
        final Section 삼성역_잠실역 = new Section(2L, 삼성역, 잠실역, 5);

        // when
        boolean actual = 강남역_삼성역.hasAllStations(삼성역_잠실역);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("입력한 역이 해당 구간의 시작역 또는 종착역에 해당한다")
    void hasStations() {
        // given
        final Section 강남역_삼성역 = new Section(1L, 강남역, 삼성역, 5);

        // when
        boolean actual = 강남역_삼성역.hasStations(강남역);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("입력한 역이 해당 구간의 시작역 또는 종착역에 해당하지 않는다")
    void hasStationsFalse() {
        // given
        final Section 강남역_삼성역 = new Section(1L, 강남역, 삼성역, 5);

        // when
        boolean actual = 강남역_삼성역.hasStations(잠실역);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("입력한 역이 해당 구간의 시작역에 해당한다")
    void hasUpStation() {
        // given
        final Section 강남역_삼성역 = new Section(1L, 강남역, 삼성역, 5);

        // when
        boolean actual = 강남역_삼성역.hasUpStation(강남역);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("입력한 역이 해당 구간의 시작역에 해당하지 않는다")
    void hasUpStation_False() {
        // given
        final Section 강남역_삼성역 = new Section(1L, 강남역, 삼성역, 5);

        // when
        boolean actual = 강남역_삼성역.hasUpStation(강남역);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("입력한 역이 해당 구간의 종착역에 해당한다")
    void hasDownStation() {
        // given
        final Section 강남역_삼성역 = new Section(1L, 강남역, 삼성역, 5);

        // when
        boolean actual = 강남역_삼성역.hasDownStation(삼성역);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("입력한 역이 해당 구간의 종착역에 해당하지 않는다")
    void hasDownStation_False() {
        // given
        final Section 강남역_삼성역 = new Section(1L, 강남역, 삼성역, 5);

        // when
        boolean actual = 강남역_삼성역.hasDownStation(잠실역);

        // then
        assertThat(actual).isFalse();
    }
}
