package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class SectionTest {

    @Test
    @DisplayName("구간은 상행역과 하행역이 있어야 생성할 수 있다.")
    void 생성() {
        // given
        Station upStation = new Station("잠실역");
        Station downStation = new Station("장지역");
        Integer distance = 10;

        // when
        Section section = Section.of(upStation, downStation, distance, null);

        // then
        assertThat(section).isNotNull();
    }

    @Test
    @DisplayName("구간은 상행역이 없다면 생성할 수 없다.")
    void 생성_실패_상행역_없음() {
        // given
        Station downStation = new Station("장지역");
        Integer distance = 10;

        // expect
        assertThatIllegalArgumentException().isThrownBy(() -> Section.of(null, downStation, distance, null));
    }


    @Test
    @DisplayName("구간길이가 1미만이면 생성할 수 없다")
    void 생성_실패_구간길이_1미만() {
        // given
        Station upStation = new Station("잠실역");
        Station downStation = new Station("장지역");
        Integer distance = 0;

        // expect
        assertThatIllegalArgumentException().isThrownBy(() -> Section.of(upStation, downStation, distance, null));
    }

}
