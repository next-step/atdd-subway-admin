package nextstep.subway.domain.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.stream.Stream;
import nextstep.subway.domain.station.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class SectionTest {

    private Station 논현역;
    private Station 신논현역;
    private int 구간_거리;

    @BeforeEach
    void setUp() {
        논현역 = new Station("논현역");
        신논현역 = new Station("신논현역");
        구간_거리 = 10;
    }

    @Test
    @DisplayName("구간 생성 검증")
    public void createSection() {
        // When
        Section 생성된_구간 = Section.of(null, 논현역, 신논현역, 구간_거리);

        // Then
        assertThat(생성된_구간).isNotNull();
    }

    @Test
    @DisplayName("상행역과 하행역이 동일한 구간 생성 시 예외")
    public void throwException_WhenAddUpStationAndDownStationIsEquals() {
        // When
        Section 생성된_구간 = Section.of(null, 논현역, 신논현역, 구간_거리);

        // Then
        assertThat(생성된_구간).isNotNull();
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("상행역 혹은 하행역 정보가 없는 구간 생성 시 예외")
    public void throwException_WhenUpStationOrDownStationIsNull(final Station 상행역, final Station 하행역) {
        // When & Then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> Section.of(null, 상행역, 하행역, 구간_거리));
    }

    private static Stream throwException_WhenUpStationOrDownStationIsNull() {
        return Stream.of(
            Arguments.of(new Station("논현역"), null),
            Arguments.of(null, new Station("신논현역")),
            Arguments.of(null, null)
        );
    }

    @Test
    @DisplayName("상행역과 하행역이 동일한 구간 생성 시 예외")
    public void throwException_WhenUpStationIsEqualToDownStation() {
        // When & Then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> Section.of(null, 논현역, 논현역, 구간_거리));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, Integer.MIN_VALUE})
    @DisplayName("유효하지 않는 길이를 가지는 구간 생성 시 에외")
    public void throwException_WhenSectionDistanceIsInValid(final int 구간_거리) {
        // When & Then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> Section.of(null, 논현역, 신논현역, 구간_거리));
    }
}
