package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionTest {
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

    @DisplayName("구간 추가 반영 - 상행선 일치")
    @Test
    void modify_upStaion() {
        Section section = new Section(교대역, 삼성역, 10);
        Section newSection = new Section(교대역, 강남역, 5);

        section.relocate(newSection);
        assertAll(
            () -> assertThat(section.getUpStation()).isEqualTo(강남역),
            () -> assertThat(section.getDownStation()).isEqualTo(삼성역),
            () -> assertThat(section.getDistance()).isEqualTo(5L)
        );
    }

    @DisplayName("구간 추가 반영 - 하행선 일치")
    @Test
    void modify_downStation() {
        Section section = new Section(교대역, 삼성역, 10);
        Section newSection = new Section(선릉역, 삼성역, 5);

        section.relocate(newSection);
        assertAll(
            () -> assertThat(section.getUpStation()).isEqualTo(교대역),
            () -> assertThat(section.getDownStation()).isEqualTo(선릉역),
            () -> assertThat(section.getDistance()).isEqualTo(5L)
        );
    }


    @DisplayName("구간 추가 반영 - 거리 기존 역 사이 거리보다 크거나 같은 경우")
    @Test
    void modify_IllegalArgumentException() {
        Section section = new Section(교대역, 삼성역, 10);
        Section newSection = new Section(교대역, 선릉역, 15);

        assertThatThrownBy(() -> section.relocate(newSection))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
