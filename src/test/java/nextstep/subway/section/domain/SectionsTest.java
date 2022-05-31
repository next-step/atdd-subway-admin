package nextstep.subway.section.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SectionsTest {
    private Section newSection;

    @BeforeEach
    void setUp() {
        this.newSection = new Section(SectionTest.강남역, SectionTest.역삼역, SectionTest.distance);
    }

    @DisplayName("비어있는 지하철 구간 목록 생성")
    @Test
    void test_create_empty() {
        Sections emptySections = Sections.empty();
        assertAll(
                () -> assertThat(emptySections).isNotNull(),
                () -> assertThat(emptySections.size()).isEqualTo(0)
        );
    }

    @DisplayName("지하철 구간 목록 생성")
    @Test
    void test_create() {
        Sections emptySections = Sections.from(Arrays.asList(this.newSection));
        assertAll(
                () -> assertThat(emptySections).isNotNull(),
                () -> assertThat(emptySections.size()).isEqualTo(1)
        );
    }

    @DisplayName("목록에 상행역과 연결된 지하철 구간 추가")
    @Test
    void test_add_up_section() {
        Section upSection = new Section(SectionTest.강남역, SectionTest.선릉역, SectionTest.distance);
        Sections sections = Sections.from(new ArrayList<>(Arrays.asList(upSection)));
        sections.add(this.newSection);
        assertAll(
                () -> assertThat(sections.size()).isEqualTo(2),
                () -> assertThat(sections.containsStation(SectionTest.강남역)).isTrue(),
                () -> assertThat(sections.containsStation(SectionTest.역삼역)).isTrue(),
                () -> assertThat(sections.containsStation(SectionTest.선릉역)).isTrue()
        );
    }
}