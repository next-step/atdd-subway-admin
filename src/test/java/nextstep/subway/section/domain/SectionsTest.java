package nextstep.subway.section.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SectionsTest {
    private Section section;

    @BeforeEach
    void setUp() {
        this.section = new Section(SectionTest.upStation, SectionTest.downStation, SectionTest.distance);
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
        Sections emptySections = Sections.from(Arrays.asList(this.section));
        assertAll(
                () -> assertThat(emptySections).isNotNull(),
                () -> assertThat(emptySections.size()).isEqualTo(1)
        );
    }

    @DisplayName("목록에 지하철 구간 추가")
    @Test
    void test_add() {
        Sections sections = Sections.empty();
        sections.add(this.section);
        assertAll(
                () -> assertThat(sections).isNotNull(),
                () -> assertThat(sections.size()).isEqualTo(1)
        );
    }
}