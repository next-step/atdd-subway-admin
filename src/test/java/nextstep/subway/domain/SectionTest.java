package nextstep.subway.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.common.Messages.DISTANCE_MINIMUM_LENGTH_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class SectionTest {

    private Station 강남역;
    private Station 역삼역;
    private int 거리;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        거리 = 10;
    }

    @Test
    @DisplayName("지하철 구간을 등록 테스트")
    void newSection() {
        Section section = new Section(거리, 강남역, 역삼역);

        Assertions.assertAll(
                () -> assertThat(section.getDistance()).isEqualTo(10),
                () -> assertThat(section.getUpStation()).isInstanceOf(Station.class),
                () -> assertThat(section.getDownStation()).isInstanceOf(Station.class)
        );
    }

    @Test
    @DisplayName("지하철 구간을 등록시 최소길이 에러")
    void distanceError() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Section(0, 강남역, 역삼역))
                .withMessageContaining(DISTANCE_MINIMUM_LENGTH_ERROR);
    }
}
