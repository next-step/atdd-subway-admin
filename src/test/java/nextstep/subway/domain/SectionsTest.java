package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SectionsTest {
    private Station 서초역 = Station.from("서초역");
    private Station 강남역 = Station.from("강남역");
    private Sections sections;

    @BeforeEach
    void setUp() {
        sections = Sections.from(Collections.singletonList(Section.from(서초역, 강남역, Distance.from(10))));
    }

    @Test
    void 역_사이에_새로운_역을_등록() {
        Station 교대역 = Station.from("교대역");
        Section newSection = Section.from(서초역, 교대역, Distance.from(5));
        sections.add(newSection);
        assertThat(sections.assignedStations()).contains(서초역, 교대역, 강남역);
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 11})
    void 추가하려는_구간의_길이가_기존_구간의_길이보다_크거나_같을경우_예외(int distance) {
        Station 교대역 = Station.from("교대역");
        Section newSection = Section.from(서초역, 교대역, Distance.from(distance));
        assertThatThrownBy(() -> sections.add(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Distance는 0이하의 값일 수 없습니다.");
    }
}
