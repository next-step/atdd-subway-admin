package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("구간 테스트")
public class SectionTest {

    @DisplayName("구간의 거리가 0 이하인 경우 예외 발생")
    @Test
    void createSectionWrong_minDistance() {
        assertThatThrownBy(() -> new Section(1L, 2L, 0))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행역ID나 하행역ID 가 없을 경우 예외 발생")
    @Test
    void createSectionWrong_emptyStationId() {
        assertThatThrownBy(() -> new Section(null, 2L, 7))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Section(1L, null, 7))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행선과 하행선이 같을경우 예외 발생")
    @Test
    void createSectionWrong_sameUpAndDown() {
        assertThatThrownBy(() -> new Section(1L, 1L, 7))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("이전 구간 확인")
    @Test
    void isBefore() {
        Section before = new Section(1L, 2L, 7);
        Section after = new Section(2L, 3L, 4);
        assertThat(before.isBefore(after)).isTrue();
    }

    @DisplayName("다음 구간 확인")
    @Test
    void isAfter() {
        Section before = new Section(1L, 2L, 7);
        Section after = new Section(2L, 3L, 4);
        assertThat(after.isAfter(before)).isTrue();
    }

    @DisplayName("구간에 포함될수 있는지 확인")
    @ParameterizedTest
    @MethodSource("newSections")
    void isBetween(Section newSection) {
        Section preSection = new Section(1L, 3L, 7);
        assertThat(newSection.isBetween(preSection)).isTrue();
    }

    static Stream<Section> newSections() {
        return Stream.of(
            new Section(1L, 2L, 4),
            new Section(2L, 3L, 4)
        );
    }

    @DisplayName("상행선 하행선이 모두 동일한지 확인")
    @Test
    void isEqualUpAndDownStation() {
        Section preSection = new Section(1L, 3L, 7);
        Section newSection = new Section(1L, 3L, 4);
        assertThat(newSection.isEqualUpAndDownStation(preSection)).isTrue();
    }

    @DisplayName("구간에 새로운 구간을 추가할 경우 확인")
    @Test
    void addIntoSection() {
        // given
        Section preSection = new Section(1L, 3L, 7);
        Section newSection = new Section(1L, 2L, 4);

        // when
        preSection.addIntoSection(newSection);

        //then
        assertThat(preSection.getDownStation().getId()).isEqualTo(2L);
        assertThat(preSection.getDistance()).isEqualTo(3);
        assertThat(newSection.getUpStation().getId()).isEqualTo(2L);
        assertThat(newSection.getDownStation().getId()).isEqualTo(3L);
        assertThat(newSection.getDistance()).isEqualTo(4);
    }
}
