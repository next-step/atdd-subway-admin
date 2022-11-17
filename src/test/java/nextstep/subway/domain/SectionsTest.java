package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SectionsTest {
    private Station WANGSIPLI;
    private Station SANGWANGSIPLI;
    private Station SINDANG;
    private Station DDP;

    @BeforeEach
    void setup() {
        WANGSIPLI = new Station(1L, "왕십리");
        SANGWANGSIPLI = new Station(2L, "상왕십리");
        SINDANG = new Station(3L, "신당");
        DDP = new Station(4L, "동대문역사문화공원");
    }

    @DisplayName("구간 추가")
    @Test
    void add() {
        Sections sections = new Sections();
        sections.addSection(new Section(WANGSIPLI, SANGWANGSIPLI, 10L));

        assertThat(sections.getStations()).contains(WANGSIPLI, SANGWANGSIPLI);
    }

    @DisplayName("구간 추가 실패 상행역 하행역 이미 등록되어 있음")
    @Test
    void add_already_contains_all() {
        Sections sections = new Sections();
        sections.addSection(new Section(WANGSIPLI, SANGWANGSIPLI, 10L));

        assertThatThrownBy(() -> sections.addSection(new Section(WANGSIPLI, SANGWANGSIPLI, 10L)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간 추가 실패 상행역 하행역 둘다 포함되어 있지 않음")
    @Test
    void add_not_contains_any() {
        Sections sections = new Sections();
        sections.addSection(new Section(WANGSIPLI, SANGWANGSIPLI, 10L));

        assertThatThrownBy(() -> sections.addSection(new Section(SINDANG, DDP, 10L)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간 추가 실패 - 거리가 기존 역 사이 길이보다 크거나 같음")
    @ParameterizedTest
    @ValueSource(longs = {10L, 20L})
    void add_distance_over(long distance) {
        Sections sections = new Sections();
        sections.addSection(new Section(WANGSIPLI, DDP, 10L));

        assertThatThrownBy(() -> sections.addSection(new Section(WANGSIPLI, SANGWANGSIPLI, distance)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("역 목록 조회")
    @Test
    void getStations() {
        Sections sections = new Sections();
        sections.addSection(new Section(WANGSIPLI, SANGWANGSIPLI, 10L));
        sections.addSection(new Section(SANGWANGSIPLI, SINDANG, 10L));
        sections.addSection(new Section(SINDANG, DDP, 10L));

        assertThat(sections.getStations()).contains(WANGSIPLI, SANGWANGSIPLI, SINDANG, DDP);
    }
}
