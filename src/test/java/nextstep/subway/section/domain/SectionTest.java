package nextstep.subway.section.domain;

import nextstep.subway.common.exception.RegisterDistanceException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SectionTest {

    private static Station 잠실역 = null;
    private static Station 잠실나루 = null;
    private static Station 강변역 = null;

    @BeforeEach
    void setUp() {
        잠실역 = new Station("잠실역");
        잠실나루 = new Station("잠실나루");
        강변역 = new Station("강변역");
    }

    @DisplayName("상행역이 동일하다")
    @Test
    void isUpStationEquals() {
        final Section 첫번째구간 = Section.of(잠실역, 잠실나루, 10);
        final Section 두번째구간 = Section.of(잠실역, 강변역, 10);

        assertThat(첫번째구간.isUpStationEquals(두번째구간)).isTrue();
    }

    @DisplayName("하행역이 동일하다")
    @Test
    void isDownStationEquals() {
        final Section 첫번째구간 = Section.of(잠실역, 잠실나루, 10);
        final Section 두번째구간 = Section.of(강변역, 잠실나루, 10);

        assertThat(첫번째구간.isDownStationEquals(두번째구간)).isTrue();
    }

    @DisplayName("등록된 구간의 상행역과 추가할 하행역이 동일하다")
    @Test
    void isUpStationAndTargetDownStationEquals() {
        final Section 첫번째구간 = Section.of(잠실역, 잠실나루, 10);
        final Section 두번째구간 = Section.of(강변역, 잠실역, 10);

        assertThat(첫번째구간.isUpStationAndTargetDownStationEquals(두번째구간)).isTrue();
    }

    @DisplayName("등록된 구간의 하행역과 추가할 상행역이 동일하다")
    @Test
    void isDownStationAndTargetUpStationEquals() {
        final Section 첫번째구간 = Section.of(잠실역, 잠실나루, 10);
        final Section 두번째구간 = Section.of(잠실나루, 강변역, 10);

        assertThat(첫번째구간.isDownStationAndTargetUpStationEquals(두번째구간)).isTrue();
    }

    @DisplayName("등록된 구간의 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정한다.")
    @Test
    void minusDistance() {
        final Section 첫번째구간 = Section.of(잠실역, 잠실나루, 10);

        final Section 두번째구간 = Section.of(잠실역, 강변역, 5);

        첫번째구간.minusDistance(두번째구간);

        assertThat(첫번째구간.getDistance()).isEqualTo(5);
    }

    @DisplayName("등록된 구간의 길와 추가할 구간의 길이가 같거나 크면 실패한다.")
    @Test
    void minusDistance_예외() {
        final Section 첫번째구간 = Section.of(잠실역, 잠실나루, 10);

        final Section 두번째구간 = Section.of(잠실역, 강변역, 10);

        assertThatThrownBy(() -> 첫번째구간.minusDistance(두번째구간))
                .isInstanceOf(RegisterDistanceException.class)
                .hasMessage("등록할 수 없는 구간입니다.");
    }

    @DisplayName("등록된 구간의 상행역을 추가한 상행역으로 변경한다.")
    @Test
    void changeUpStation() {
        final Section 첫번째구간 = Section.of(잠실역, 잠실나루, 10);
        final Section 두번째구간 = Section.of(강변역, 잠실역, 10);

        첫번째구간.changeUpStation(두번째구간);

        assertThat(첫번째구간.getUpStation()).isEqualTo(강변역);
    }

    @DisplayName("등록된 구간의 하행역을 추가한 하행역으로 변경한다.")
    @Test
    void changeDownStation() {
        final Section 첫번째구간 = Section.of(잠실역, 잠실나루, 10);
        final Section 두번째구간 = Section.of(잠실나루, 강변역, 10);

        첫번째구간.changeDownStation(두번째구간);

        assertThat(첫번째구간.getDownStation()).isEqualTo(강변역);
    }

    @DisplayName("구간 사이의 역을 제거히고 거리는 두 구간의 합으로 정한다.")
    @Test
    void mergeSection() {
        final Section 첫번째구간 = Section.of(잠실역, 잠실나루, 10);
        final Section 두번째구간 = Section.of(잠실나루, 강변역, 10);

        첫번째구간.merge(두번째구간);

        assertAll(
                () -> assertThat(첫번째구간.getDownStation()).isEqualTo(강변역),
                () -> assertThat(첫번째구간.getDistance()).isEqualTo(20)
        );
    }
}
