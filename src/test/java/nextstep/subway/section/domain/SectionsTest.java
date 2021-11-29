package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionsTest {
    private List<Section> 구간목록 = new ArrayList<>();
    private Line 노선 = null;
    private static Section 등록구간 = null;
    private static Station 잠실역 = null;
    private static Station 강변역 = null;

    @BeforeEach
    void setUp() {
        잠실역 = new Station(1L, "잠실역", 노선);
        강변역 = new Station(3L, "강변역", 노선);
        등록구간 = new Section(1L, 노선, 잠실역, 강변역, 10);

        구간목록.add(등록구간);
        노선 = Line.of("2호선", "green", Sections.from(구간목록));
    }

    @DisplayName("상행역을 추가한다.")
    @Test
    void add_상행역() {
        final Station 잠실새내역 = new Station(4L, "잠실새내역", 노선);
        final Section 추가구간 = new Section(2L, 노선, 잠실새내역, 잠실역, 10);

        final Sections sections = Sections.from(노선.getSections());

        sections.add(추가구간, 노선);

        assertAll(
                () -> assertThat(sections.getSections()).hasSize(2),
                () -> assertThat(sections.getSections().get(0).getUpStation()).isEqualTo(잠실새내역)
        );
    }

    @DisplayName("하행역을 추가한다.")
    @Test
    void add_하행역() {
        final Station 잠실나루역 = new Station(4L, "잠실나루역", 노선);
        final Section 추가구간 = new Section(2L, 노선, 강변역, 잠실나루역, 10);

        final Sections sections = Sections.from(노선.getSections());

        sections.add(추가구간, 노선);

        assertAll(
                () -> assertThat(sections.getSections()).hasSize(2),
                () -> assertThat(sections.getSections().get(1).getDownStation()).isEqualTo(잠실나루역)
        );
    }

    @DisplayName("상행역과 하행역 사이를 추가한다.")
    @Test
    void add_중간역() {
        final Station 잠실나루역 = new Station(4L, "잠실나루역", 노선);
        final Section 추가구간 = new Section(2L, 노선, 잠실역, 잠실나루역, 5);

        final Sections sections = Sections.from(노선.getSections());

        sections.add(추가구간, 노선);

        assertAll(
                () -> assertThat(sections.getSections()).hasSize(2),
                () -> assertThat(sections.getSections().get(0).getDownStation()).isEqualTo(잠실나루역),
                () -> assertThat(sections.getSections().get(0).getDistance()).isEqualTo(5)
        );
    }

    @DisplayName("등록된 구간의 상행역과 추가할 하행역이 동일하다.")
    @Test
    void add_예외1() {
        final Section 추가구간 = new Section(2L, 노선, 잠실역, 강변역, 5);

        final Sections sections = Sections.from(노선.getSections());

        assertThatThrownBy(() -> sections.add(추가구간, 노선))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행, 하행 역 모두가 포함되어 있어 등록할 수 없습니다.");
    }

    @DisplayName("등록된 구간의 상행역과 추가할 하행역이 동일하다.")
    @Test
    void add_예외2() {
        final Station 잠실나루역 = new Station(4L, "잠실나루역", 노선);
        final Station 잠실새내역 = new Station(5L, "잠실새내역", 노선);
        final Section 추가구간 = new Section(2L, 노선, 잠실나루역, 잠실새내역, 5);

        final Sections sections = Sections.from(노선.getSections());

        assertThatThrownBy(() -> sections.add(추가구간, 노선))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행, 하행 역 모두가 포함되지 않아서 등록할 수 없습니다.");
    }

    @DisplayName("등록된 구간의 길와 추가할 구간의 길이가 같거나 크면 실패한다.")
    @Test
    void add_예외3() {
        final Station 잠실나루역 = new Station(4L, "잠실나루역", 노선);
        final Section 추가구간 = new Section(2L, 노선, 잠실역, 잠실나루역, 10);

        final Sections sections = Sections.from(노선.getSections());

        assertThatThrownBy(() -> sections.add(추가구간, 노선))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("등록할 수 없는 구간입니다.");
    }
}
