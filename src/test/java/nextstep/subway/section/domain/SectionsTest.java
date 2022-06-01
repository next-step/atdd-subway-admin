package nextstep.subway.section.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SectionsTest {
    public static final Distance addDistance = Distance.from(4);
    private Section newSection;

    @BeforeEach
    void setUp() {
        this.newSection = new Section(SectionTest.강남역, SectionTest.역삼역, SectionTest.distance);
    }

    @DisplayName("비어있는 지하철 구간 목록 생성")
    @Test
    void test_create_empty() {
        // given & when
        Sections emptySections = Sections.empty();
        // then
        assertThanNotNullAndSize(emptySections, 0);
    }

    @DisplayName("지하철 구간 목록 생성")
    @Test
    void test_create() {
        // given & when
        Sections emptySections = Sections.from(Arrays.asList(this.newSection));
        // then
        assertThanNotNullAndSize(emptySections, 1);
    }

    @DisplayName("목록에 상행역과 연결된 지하철 구간 추가 : 강남역-(선릉역)-역삼역")
    @Test
    void test_add_up_section() {
        // given
        Section upSection = new Section(SectionTest.강남역, SectionTest.선릉역, addDistance);
        Sections sections = Sections.from(new ArrayList<>(Arrays.asList(upSection)));
        // when
        sections.add(this.newSection);
        // then
        assertThatSizeAndContainsStation(sections, 2);
    }

    @DisplayName("목록에 하행역과 연결된 지하철 구간 추가 : 강남역-(선릉역)-역삼역")
    @Test
    void test_add_down_section() {
        // given
        Section upSection = new Section(SectionTest.선릉역, SectionTest.역삼역, addDistance);
        Sections sections = Sections.from(new ArrayList<>(Arrays.asList(upSection)));
        // when
        sections.add(this.newSection);
        // then
        assertThatSizeAndContainsStation(sections, 2);
    }

    @DisplayName("목록에 새로운 상행 종점 지하철 구간 추가 : (선릉역)-강남역-역삼역")
    @Test
    void test_add_edge_up_section() {
        // given
        Section upSection = new Section(SectionTest.선릉역, SectionTest.강남역, addDistance);
        Sections sections = Sections.from(new ArrayList<>(Arrays.asList(upSection)));
        // when
        sections.add(this.newSection);
        // then
        assertThatSizeAndContainsStation(sections, 2);
    }

    @DisplayName("목록에 새로운 하행 종점 지하철 구간 추가 : 강남역-역삼역-(선릉역)")
    @Test
    void test_add_edge_down_section() {
        // given
        Section upSection = new Section(SectionTest.역삼역, SectionTest.선릉역, addDistance);
        Sections sections = Sections.from(new ArrayList<>(Arrays.asList(upSection)));
        // when
        sections.add(this.newSection);
        // then
        assertThatSizeAndContainsStation(sections, 2);
    }

    private void assertThanNotNullAndSize(Sections sections, int expectedSize) {
        assertAll(
                () -> assertThat(sections).isNotNull(),
                () -> assertThat(sections.size()).isEqualTo(expectedSize)
        );
    }

    private void assertThatSizeAndContainsStation(Sections sections, int expectedSize) {
        assertAll(
                () -> assertThat(sections.size()).isEqualTo(expectedSize),
                () -> assertThat(sections.containsStation(SectionTest.강남역)).isTrue(),
                () -> assertThat(sections.containsStation(SectionTest.역삼역)).isTrue(),
                () -> assertThat(sections.containsStation(SectionTest.선릉역)).isTrue()
        );
    }
}