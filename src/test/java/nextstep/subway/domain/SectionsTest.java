package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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
        sections.addSection(new Section(1L, WANGSIPLI, SANGWANGSIPLI, 10L));

        assertThat(sections.allStations().getList()).contains(WANGSIPLI, SANGWANGSIPLI);
    }

    @DisplayName("구간 추가 실패 상행역 하행역 이미 등록되어 있음")
    @Test
    void add_already_contains_all() {
        Sections sections = new Sections();
        sections.addSection(new Section(1L, WANGSIPLI, SANGWANGSIPLI, 10L));

        assertThatThrownBy(() -> sections.addSection(new Section(WANGSIPLI, SANGWANGSIPLI, 10L)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간 추가 실패 상행역 하행역 둘다 포함되어 있지 않음")
    @Test
    void add_not_contains_any() {
        Sections sections = new Sections();
        sections.addSection(new Section(1L, WANGSIPLI, SANGWANGSIPLI, 10L));

        assertThatThrownBy(() -> sections.addSection(new Section(SINDANG, DDP, 10L)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간 추가 실패 - 거리가 기존 역 사이 길이보다 크거나 같음")
    @ParameterizedTest
    @ValueSource(longs = {10L, 20L})
    void add_distance_over(long distance) {
        Sections sections = new Sections();
        sections.addSection(new Section(1L, WANGSIPLI, DDP, 10L));

        assertThatThrownBy(() -> sections.addSection(new Section(WANGSIPLI, SANGWANGSIPLI, distance)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("역 목록 조회")
    @Test
    void getStations() {
        Sections sections = new Sections();
        sections.addSection(new Section(1L, WANGSIPLI, SANGWANGSIPLI, 10L));
        sections.addSection(new Section(2L, SANGWANGSIPLI, SINDANG, 10L));
        sections.addSection(new Section(3L, SINDANG, DDP, 10L));

        assertThat(sections.allStations().getList()).contains(WANGSIPLI, SANGWANGSIPLI, SINDANG, DDP);
    }

    @DisplayName("구간 삭제 - 상행 종점")
    @Test
    void removeSection_upStation() {
        Sections sections = new Sections();
        sections.addSection(new Section(1L, WANGSIPLI, SANGWANGSIPLI, 10L));
        sections.addSection(new Section(2L, SANGWANGSIPLI, SINDANG, 10L));

        sections.removeByStation(WANGSIPLI);

        assertThat(sections.getList()).hasSize(1);
        assertThat(sections.allStations().getList()).contains(SANGWANGSIPLI, SINDANG);
        assertThat(sections.allStations().getList()).doesNotContain(WANGSIPLI);
    }

    @DisplayName("구간 삭제 - 하행 종점")
    @Test
    void removeSection_downStation() {
        Sections sections = new Sections();
        sections.addSection(new Section(1L, WANGSIPLI, SANGWANGSIPLI, 10L));
        sections.addSection(new Section(2L, SANGWANGSIPLI, SINDANG, 10L));

        sections.removeByStation(SINDANG);

        assertThat(sections.getList()).hasSize(1);
        assertThat(sections.allStations().getList()).contains(WANGSIPLI, SANGWANGSIPLI);
        assertThat(sections.allStations().getList()).doesNotContain(SINDANG);
    }

    @DisplayName("구간 삭제 - 가운데 역")
    @Test
    void removeSection_midStation() {
        Sections sections = new Sections();
        sections.addSection(new Section(1L, WANGSIPLI, SANGWANGSIPLI, 10L));
        sections.addSection(new Section(2L, SANGWANGSIPLI, SINDANG, 10L));

        sections.removeByStation(SANGWANGSIPLI);

        assertThat(sections.getList()).hasSize(1);
        assertThat(sections.allStations().getList()).contains(WANGSIPLI, SINDANG);
        assertThat(sections.allStations().getList()).doesNotContain(SANGWANGSIPLI);
    }

    @DisplayName("구간 삭제 실패 - 노선에 등록되어있지 않은 역을 제거")
    @Test
    void removeSection_notContainsStation() {
        Sections sections = new Sections();
        sections.addSection(new Section(1L, WANGSIPLI, SANGWANGSIPLI, 10L));
        sections.addSection(new Section(2L, SANGWANGSIPLI, SINDANG, 10L));

        assertThatThrownBy(() -> sections.removeByStation(DDP))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간 삭제 실패 - 구간이 하나인 노선에서 역을 제거")
    @Test
    void removeSection_onlySectionStations() {
        Sections sections = new Sections();
        sections.addSection(new Section(1L, WANGSIPLI, SANGWANGSIPLI, 10L));

        assertAll(
            () -> assertThatThrownBy(() -> sections.removeByStation(WANGSIPLI))
                .isInstanceOf(IllegalArgumentException.class),
            () -> assertThatThrownBy(() -> sections.removeByStation(SANGWANGSIPLI))
                .isInstanceOf(IllegalArgumentException.class)
        );
    }
}
