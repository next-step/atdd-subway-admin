package nextstep.subway.line;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.line.SectionTestFixture.강남역;
import static nextstep.subway.line.SectionTestFixture.역삼역;
import static nextstep.subway.line.SectionTestFixture.이호선;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("구간 테스트")
class SectionTest {

    @DisplayName("생성 확인")
    @Test
    void 생성_확인() {
        // given
        Line 이호선 = 이호선();
        Station 강남역 = 강남역();
        Station 역삼역 = 역삼역();

        // when
        Section section = Section.of(이호선, 강남역, 역삼역, 10);

        // then
        assertThat(section)
                .isNotNull();
    }

    @DisplayName("중복된 역으로 생성")
    @Test
    void 중복된_역으로_생성() {
        // given
        Line 이호선 = 이호선();
        Station 강남역 = 강남역();

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> Section.of(이호선, 강남역, 강남역, 10);

        // then
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("거리가 0")
    @Test
    void 거리가_0() {
        // given
        Line 이호선 = 이호선();
        Station 강남역 = 강남역();
        Station 역삼역 = 역삼역();

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> Section.of(이호선, 강남역, 역삼역, 0);

        // then
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(IllegalArgumentException.class);
    }
}
