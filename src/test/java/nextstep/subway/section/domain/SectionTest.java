package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionTest {

    @DisplayName("두개의 역으로 지하철 구간을 생성할 수 있다")
    @Test
    void 두개의_역으로_지하철_구간을_생성할_수_있다() {
        //given
        final Station upStation = new Station("강남");
        final Station downStation = new Station("삼성");
        final Distance distance = new Distance(7);

        //when
        final Section section = new Section(upStation, downStation, distance);

        //then
        assertAll(
            () -> assertThat(section).isNotNull(),
            () -> assertThat(section.getUpStation()).isEqualTo(upStation),
            () -> assertThat(section.getDownStation()).isEqualTo(downStation)
        );
    }

    @DisplayName("같은 역으로 지하철 구간을 생성할 수 없다")
    @Test
    void 같은_역으로_지하철_구간을_생성할_수_없다() {
        //given
        final Station upStation = new Station("강남");
        final Station downStation = new Station("삼성");
        final Distance distance = new Distance(7);

        //when then
        assertAll(
            () -> assertThatIllegalArgumentException().isThrownBy(() -> new Section(upStation, upStation, distance)),
            () -> assertThatIllegalArgumentException().isThrownBy(() -> new Section(downStation, downStation, distance))
        );
    }
}