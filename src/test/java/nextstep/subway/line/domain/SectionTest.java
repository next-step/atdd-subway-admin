package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.line.domain.SectionTestFixture.강남역;
import static nextstep.subway.line.domain.SectionTestFixture.역삼역;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("구간 테스트")
class SectionTest {

    @DisplayName("생성 확인")
    @Test
    void 생성_확인() {
        // given
        Station 강남역 = 강남역();
        Station 역삼역 = 역삼역();

        // when
        Section section = Section.of(강남역, 역삼역, 10);

        // then
        assertThat(section)
                .isNotNull();
    }

    @DisplayName("중복된 역으로 생성")
    @Test
    void 중복된_역으로_생성() {
        // given
        Station 강남역 = 강남역();

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> Section.of(강남역, 강남역, 10);

        // then
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(IllegalArgumentException.class);
    }
}
