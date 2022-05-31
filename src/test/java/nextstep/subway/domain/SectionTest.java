package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    private final static Station station1 = new Station(1L, "강남역");
    private final static Station station2 = new Station(2L, "삼성역");
    private final static Station station3 = new Station(3L, "잠실역");

    @Test
    @DisplayName("changeStationInfo의 결과 현재 저장된 구간 정보가 변경된다")
    void changeStationInfo() {
        // given
        final Section section = new Section(1L, station1, station3, 10);
        final Section section2 = new Section(2L, station2, station3, 5);

        // when
        section.changeStationInfo(section2);

        // then
        assertThat(section.getLineStations()).containsExactly(station1, station2);
    }

    @Test
    @DisplayName("changeStationInfo의 결과 현재 저장된 구간보다 변경하려는 구간의 길이가 같거나 긴 경우 오류를 반환한다")
    void changeStationInfoError() {
        // given
        final Section section = new Section(1L, station1, station3, 5);
        final Section section2 = new Section(2L, station2, station3, 5);

        // when
        assertThatIllegalArgumentException().isThrownBy(
                () -> section.changeStationInfo(section2)
        );
    }

}
