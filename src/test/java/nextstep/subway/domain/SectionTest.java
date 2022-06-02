package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    private final static Station 강남역 = new Station(1L, "강남역");
    private final static Station 삼성역 = new Station(2L, "삼성역");
    private final static Station 잠실역 = new Station(3L, "잠실역");

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
    @DisplayName("노선의 중간역을 삭제하는 경우 기존 종착역 정보가 다음 종착역으로 변경된다")
    void combineStationInfo() {
        // given
        final Section 강남역_삼성역 = new Section(1L, 강남역, 삼성역, 5);
        final Section 강남역_잠실역 = new Section(2L, 강남역, 잠실역, 5);

        // when
        강남역_삼성역.combineStationInfo(강남역_잠실역);

        // then
        assertThat(강남역_삼성역.getLineStations()).containsExactly(강남역, 잠실역);
    }

    @Test
    @DisplayName("노선의 중간역을 삭제하는 경우 거리가 다음 종착역과의 거리 합으로 변경된다")
    void combineDistance() {
        // given
        final Section 강남역_삼성역 = new Section(1L, 강남역, 삼성역, 5);
        final Section 강남역_잠실역 = new Section(2L, 강남역, 잠실역, 5);

        // when
        강남역_삼성역.combineStationInfo(강남역_잠실역);

        // then
        assertThat(강남역_삼성역.getDistance().getDistance()).isEqualTo(10);
    }
}
