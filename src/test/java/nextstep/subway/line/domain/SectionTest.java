package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("구간")
class SectionTest {
    private Section 강남_선릉_구간;
    private Section 강남_역삼_구간;
    private Section 역삼_선릉_구간;
    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        선릉역 = new Station("선릉역");

        강남_선릉_구간 = Section.of(강남역, 선릉역, 15);
        강남_역삼_구간 = Section.of(강남역, 역삼역, 5);
        역삼_선릉_구간 = Section.of(역삼역, 선릉역, 10);
    }

    @Test
    @DisplayName("입력받은 아이디를 가진 역이 구간에 포함되는지 확인할 수 있다.")
    void 같은_역_포함_여부() {
        assertAll(() -> assertThat(강남_선릉_구간.hasStation(강남역)).isTrue(),
                () -> assertThat(강남_선릉_구간.hasStation(역삼역)).isFalse());
    }

    @Test
    @DisplayName("입력받은 구간과 같은 상행역 또는 하행역을 가지는지 확인할 수 있다.")
    void 같은_상행_또는_하행역_포함_여부() {
        assertAll(() -> assertThat(역삼_선릉_구간.hasSameUpOrDownStation(강남_역삼_구간)).isFalse(),
                () -> assertThat(역삼_선릉_구간.hasSameUpOrDownStation(강남_선릉_구간)).isTrue());
    }

    // 기존 강남 선릉
    // 강남 역삼
    // 신규 역삼 선릉
    @Test
    @DisplayName("입력받은 구간과 상행역이 같은 경우 상행역을 입력받은 구간의 하행역으로 변경한다.")
    void 상행역이_같은_구간_업데이트() {
        강남_선릉_구간.update(강남_역삼_구간);
        assertThat(강남_선릉_구간).isEqualTo(역삼_선릉_구간);
    }

    // 기존 강남 선릉
    // 역삼 선릉
    // 변경 강남 역삼
    @Test
    @DisplayName("입력받은 구간과 하행역이 같은 경우 하행역을 입력받은 구간의 상행역으로 변경한다.")
    void 하행역이_같은_구간_업데이트() {
        강남_선릉_구간.update(역삼_선릉_구간);
        assertThat(강남_선릉_구간).isEqualTo(강남_역삼_구간);
    }

    @Test
    @DisplayName("상행역 또는 하행역은 같지만 새 구간의 거리가 기존 구간의 거리보다 크거나 같으면 예외를 발생시킨다.")
    void 상행역_또는_하행역이_같은_구간_업데이트시_거리_검증() {
        Station 삼성역 = new Station("삼성역");
        Section 강남_삼성_구간 = Section.of(강남역, 삼성역, 15);

        assertThatThrownBy(() -> 강남_선릉_구간.update(강남_삼성_구간)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("동일한 상하행역을 가진 구ㅜ간을 추가하면 예외를 발생시킨다.")
    void 동일한_상하행역을_가진_구간_업데이트() {
        Section 또다른_강남_선릉_구간 = Section.of(강남역, 선릉역, 1);
        assertThatThrownBy(() -> 강남_선릉_구간.update(또다른_강남_선릉_구간)).isInstanceOf(IllegalArgumentException.class);
    }
}
