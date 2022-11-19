package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {
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

    @Test
    void 동등성() {
        assertAll(
            () -> assertThat(new Section(1L, WANGSIPLI, DDP, 10L)).isEqualTo(new Section(1L, WANGSIPLI, SINDANG, 20L)),
            () -> assertThat(new Section(1L, WANGSIPLI, DDP, 10L)).isNotEqualTo(new Section(2L, WANGSIPLI, DDP, 10L))
        );
    }

    @DisplayName("구간 추가 반영 - 상행선 일치")
    @Test
    void modify_upStaion() {
        Section section = new Section(WANGSIPLI, DDP, 10L);
        Section newSection = new Section(WANGSIPLI, SANGWANGSIPLI, 5L);

        section.modify(newSection);
        assertAll(
            () -> assertThat(section.getUpStation()).isEqualTo(SANGWANGSIPLI),
            () -> assertThat(section.getDownStation()).isEqualTo(DDP),
            () -> assertThat(section.getDistance()).isEqualTo(Distance.of(5L))
        );
    }

    @DisplayName("구간 추가 반영 - 하행선 일치")
    @Test
    void modify_downStation() {
        Section section = new Section(WANGSIPLI, DDP, 10L);
        Section newSection = new Section(SINDANG, DDP, 5L);

        section.modify(newSection);
        assertAll(
            () -> assertThat(section.getUpStation()).isEqualTo(WANGSIPLI),
            () -> assertThat(section.getDownStation()).isEqualTo(SINDANG),
            () -> assertThat(section.getDistance()).isEqualTo(Distance.of(5L))
        );
    }

    @DisplayName("구간 추가 반영 - 변경사항 없음")
    @Test
    void modify_() {
        Section section = new Section(WANGSIPLI, SANGWANGSIPLI, 10L);
        Section newSection = new Section(SINDANG, DDP, 5L);

        section.modify(newSection);
        assertAll(
            () -> assertThat(section.getUpStation()).isEqualTo(WANGSIPLI),
            () -> assertThat(section.getDownStation()).isEqualTo(SANGWANGSIPLI),
            () -> assertThat(section.getDistance()).isEqualTo(Distance.of(10L))
        );
    }

    @DisplayName("구간 추가 반영 - 거리 기존 역 사이 거리보다 크거나 같은 경우 - IllegalArgumentException")
    @Test
    void modify_IllegalArgumentException() {
        Section section = new Section(WANGSIPLI, DDP, 10L);
        Section newSection = new Section(WANGSIPLI, SINDANG, 15L);

        assertThatThrownBy(() -> section.modify(newSection))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간 병합")
    @Test
    void merge() {
        Section upSection = new Section(WANGSIPLI, SANGWANGSIPLI, 10L);
        Section downSection = new Section(SANGWANGSIPLI, SINDANG, 10L);

        Section combine = upSection.merge(downSection);
        assertThat(combine.getUpStation()).isEqualTo(WANGSIPLI);
        assertThat(combine.getDownStation()).isEqualTo(SINDANG);
        assertThat(combine.getDistance()).isEqualTo(Distance.of(20L));
    }
}
