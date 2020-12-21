package nextstep.subway.line.domain;

import nextstep.subway.line.domain.exceptions.NotExistSectionAddPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionAddPolicyTest {
    @DisplayName("상행역, 하행역의 일치 여부에 따라 알맞는 역 추가 정책을 찾을 수 있다.")
    @ParameterizedTest
    @MethodSource("findTestResource")
    void findTest(boolean isUpStationSame, boolean isDownStationSame, SectionAddPolicy expectedPolicy) {
        SectionAddPolicy sectionAddPolicy = SectionAddPolicy.find(isUpStationSame, isDownStationSame);

        assertThat(sectionAddPolicy).isEqualTo(expectedPolicy);
    }

    public static Stream<Arguments> findTestResource() {
        return Stream.of(
                Arguments.of(true, false, SectionAddPolicy.ADD_WITH_UP_STATION),
                Arguments.of(false, true, SectionAddPolicy.ADD_WITH_DOWN_STATION)
        );
    }

    @DisplayName("존재하지 않는 경우로 정책 탐색 시 예외 발생")
    @ParameterizedTest
    @MethodSource("findFailTestResource")
    void findFailTest(boolean isUpStationSame, boolean isDownStationSame) {
        assertThatThrownBy(() -> {
            SectionAddPolicy.find(isUpStationSame, isDownStationSame);
        }).isInstanceOf(NotExistSectionAddPolicy.class);
    }
    public static Stream<Arguments> findFailTestResource() {
        return Stream.of(
                Arguments.of(false, false),
                Arguments.of(true, true)
        );
    }
}