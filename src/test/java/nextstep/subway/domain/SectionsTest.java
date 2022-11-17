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
        assertThat(sections.assignedStations()).containsExactly(서초역, 교대역, 강남역);
    }

    @Test
    void 새로운_역을_상행_종점으로_등록() {
        Station 방배역 = Station.from("방배역");
        sections.add(Section.from(방배역, 서초역, Distance.from(5)));
        assertThat(sections.assignedStations()).containsExactly(방배역, 서초역, 강남역);
    }

    @Test
    void 새로운_역을_하행_종점으로_등록() {
        Station 역삼역 = Station.from("역삼역");
        sections.add(Section.from(강남역, 역삼역, Distance.from(5)));
        assertThat(sections.assignedStations()).containsExactly(서초역, 강남역, 역삼역);
    }

    @Test
    void 가운데_역을_제거() {
        Station 교대역 = Station.from("교대역");
        sections.add(Section.from(서초역, 교대역, Distance.from(5)));
        assertThat(sections.assignedStations()).containsExactly(서초역, 교대역, 강남역);
        sections.delete(교대역);
        assertThat(sections.assignedStations()).containsExactly(서초역, 강남역);
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

    @Test
    void 추가하려는_상행역과_하행역이_모두_등록되어_있는경우_등록_불가() {
        Section newSection = Section.from(서초역, 강남역, Distance.from(15));
        assertThatThrownBy(() -> sections.add(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 이미 모두 노선에 등록되어 있습니다.");
    }

    @Test
    void 상행역과_하행역_모두_노선에_포함되지_않는경우_등록_불가() {
        Section newSection = Section.from(Station.from("뚝섬역"), Station.from("성수역"), Distance.from(15));
        assertThatThrownBy(() -> sections.add(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역 모두 노선에 포함되어 있지 않습니다.");
    }
}
