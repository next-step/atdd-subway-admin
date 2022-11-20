package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionsTest {
    private Station 교대역;
    private Station 강남역;
    private Station 선릉역;
    private Station 삼성역;

    @BeforeEach
    void setup() {
        교대역 = new Station(1L, "교대역");
        강남역 = new Station(2L, "강남역");
        선릉역 = new Station(3L, "선릉역");
        삼성역 = new Station(4L, "삼성역");
    }

    @DisplayName("구간 추가")
    @Test
    void add() {
        Sections sections = new Sections();
        sections.add(new Section(교대역, 강남역, 10));

        assertThat(sections.getStations()).contains(교대역, 강남역);
    }

    @DisplayName("구간 추가 실패 상행역 하행역 이미 등록되어 있음")
    @Test
    void add_already_contains_all() {
        Sections sections = new Sections();
        sections.add(new Section(교대역, 강남역, 10));

        assertThatThrownBy(() -> sections.add(new Section(교대역, 강남역, 10)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간 추가 실패 상행역 하행역 둘다 포함되어 있지 않음")
    @Test
    void add_not_contains_any() {
        Sections sections = new Sections();
        sections.add(new Section(교대역, 강남역, 10));

        assertThatThrownBy(() -> sections.add(new Section(선릉역, 삼성역, 10)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간 추가 실패 - 거리가 기존 역 사이 길이보다 크거나 같음")
    @ParameterizedTest
    @ValueSource(ints = {10, 20})
    void add_distance_over(int distance) {
        Sections sections = new Sections();
        sections.add(new Section(교대역, 삼성역, 10));

        assertThatThrownBy(() -> sections.add(new Section(교대역, 강남역, distance)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간 삭제 - 상행 종점")
    @Test
    void remove_section_up_station() {
        Sections sections = new Sections();
        sections.add(new Section(1L, 교대역, 강남역, 10));
        sections.add(new Section(2L, 강남역, 선릉역, 10));

        sections.removeSectionByStation(교대역);

        assertAll(
            () -> assertThat(sections.getStations()).contains(강남역, 선릉역),
            () -> assertThat(sections.getStations()).doesNotContain(교대역)
        );
    }

    @DisplayName("구간 삭제 - 하행 종점")
    @Test
    void remove_section_down_station() {
        Sections sections = new Sections();
        sections.add(new Section(1L, 교대역, 강남역, 10));
        sections.add(new Section(2L, 강남역, 선릉역, 10));

        sections.removeSectionByStation(선릉역);

        assertAll(
            () -> assertThat(sections.getStations()).contains(강남역, 교대역),
            () -> assertThat(sections.getStations()).doesNotContain(선릉역)
        );
    }

    @DisplayName("구간 삭제 - 가운데 역")
    @Test
    void remove_section_mid_station() {
        Sections sections = new Sections();
        sections.add(new Section(1L, 교대역, 강남역, 10));
        sections.add(new Section(2L, 강남역, 선릉역, 10));

        sections.removeSectionByStation(강남역);
        assertAll(
            () -> assertThat(sections.getStations()).contains(교대역, 선릉역),
            () -> assertThat(sections.getStations()).doesNotContain(강남역)
        );
    }

    @DisplayName("구간 삭제 실패 - 노선에 등록되어있지 않은 역을 제거")
    @Test
    void remove_section_not_contains_station() {
        Sections sections = new Sections();
        sections.add(new Section(1L, 교대역, 강남역, 10));
        sections.add(new Section(2L, 강남역, 선릉역, 10));

        assertThatThrownBy(() -> sections.removeSectionByStation(삼성역))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간 삭제 실패 - 구간이 하나인 노선에서 역을 제거")
    @Test
    void remove_section_only_section_stations() {
        Sections sections = new Sections();
        sections.add(new Section(1L, 교대역, 강남역, 10));

        assertThatThrownBy(() -> sections.removeSectionByStation(교대역))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("구간 병합")
    @Test
    void merge() {
        Section upSection = new Section(1L, 교대역, 강남역, 10);
        Section downSection = new Section(2L, 강남역, 선릉역, 10);

        Section combine = upSection.merge(downSection);
        assertAll(
            () -> assertThat(combine.getUpStation()).isEqualTo(교대역),
            () -> assertThat(combine.getDownStation()).isEqualTo(선릉역),
            () -> assertThat(combine.getDistance()).isEqualTo(20)
        );
    }
}
