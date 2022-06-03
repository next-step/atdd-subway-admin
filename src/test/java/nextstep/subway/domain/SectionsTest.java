package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {
    Sections sections;
    @BeforeEach
    void setUp() {
        sections = new Sections(new Section(1L, LineTest.L1, StationTest.S1, StationTest.S3, 50));
    }

    @Test
    @DisplayName("강남역-역삼역 구간을 등록한다")
    void add() {
        sections.add(LineTest.L1, StationTest.S1, StationTest.S2, 40);
        assertThat(sections.stations()).hasSize(3);
        assertThat(sections.stations()).contains(StationTest.S2);
    }

    @Test
    @DisplayName("동일한 구간을 등록하면 예외가 발생한다")
    void addSameSectionException() {
        assertThatThrownBy(() -> sections.add(LineTest.L1, StationTest.S1, StationTest.S3, 40))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("등록된 구간입니다.");
    }

    @Test
    @DisplayName("연결된 구간이 없으면 예외가 발생한다")
    void addNoMatchedSectionException() {
        assertThatThrownBy(() -> sections.add(LineTest.L1, StationTest.S2, StationTest.S4, 40))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("등록할 수 있는 구간이 없습니다.");
    }

    @Test
    @DisplayName("상행->하행 순서로 지하철역 리스트를 가져온다")
    void stations() {
        sections.add(LineTest.L1, StationTest.S1, StationTest.S2, 40);
        sections.add(LineTest.L1, StationTest.S3, StationTest.S4, 40);
        assertThat(sections.stations().get(0).getName()).isEqualTo("강남역");
        assertThat(sections.stations().get(1).getName()).isEqualTo("력삼역");
        assertThat(sections.stations().get(2).getName()).isEqualTo("선릉역");
        assertThat(sections.stations().get(3).getName()).isEqualTo("삼성역");
    }
}
