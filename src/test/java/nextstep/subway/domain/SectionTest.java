package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SectionTest {
    @Test
    void 구간_재조정() {
        Section existSection = Section.from(Station.from("서초역"), Station.from("강남역"), Distance.from(10));
        Section newSection = Section.from(Station.from("서초역"), Station.from("교대역"), Distance.from(5));

        existSection.reorganize(newSection);

        assertThat(existSection).isEqualTo(Section.from(Station.from("교대역"), Station.from("강남역"), Distance.from(5)));
        assertThat(newSection).isEqualTo(Section.from(Station.from("서초역"), Station.from("교대역"), Distance.from(5)));
    }

    @Test
    void 구간_재조정시_거리가_0이하인_경우_예외() {
        Section existSection = Section.from(Station.from("서초역"), Station.from("강남역"), Distance.from(10));
        Section newSection = Section.from(Station.from("서초역"), Station.from("교대역"), Distance.from(10));

        assertThatThrownBy(() -> existSection.reorganize(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Distance는 0이하의 값일 수 없습니다.");
    }

    @Test
    void 상행역과_하행역이_동일한지_검증() {
        Section existSection = Section.from(Station.from("서초역"), Station.from("강남역"), Distance.from(10));
        Section newSection = Section.from(Station.from("서초역"), Station.from("강남역"), Distance.from(10));
        assertThat(existSection.isSameUpDownStation(newSection)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void 구간_생성_시_길이가_0이하인_경우_예외(int distance) {
        assertThatThrownBy(() -> Section.from(Station.from("서초역"), Station.from("강남역"), Distance.from(distance)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Distance는 0이하의 값일 수 없습니다.");
    }
}
