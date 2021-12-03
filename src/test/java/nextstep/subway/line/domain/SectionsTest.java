package nextstep.subway.line.domain;

import nextstep.subway.common.exception.EmptySectionException;
import nextstep.subway.common.exception.InvalidDuplicatedSection;
import nextstep.subway.common.exception.MinimumRemovableSectionSizeException;
import nextstep.subway.common.exception.NotContainsStationException;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("구간 목록 도메인 관련")
class SectionsTest {
    private Sections sections;
    private Station 강남역;
    private Station 신사역;
    private Line 호선2;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        신사역 = new Station("신사역");
        호선2 = Line.of("2호선", "green", 강남역, 신사역, 9);
        final Section 강남_신사_구간 = Section.of(호선2, 강남역, 신사역, 9);
        sections = new Sections();
        sections.getSections().add(강남_신사_구간);
    }

    @DisplayName("구간 목록을 저장한다.")
    @Test
    void createSections() {
        // when
        final List<Section> sections = this.sections.getSections();

        // then
        assertAll(
                () -> assertThat(sections.get(0).getUpStation()).isEqualTo(new Station("강남역")),
                () -> assertThat(sections.get(0).getDownStation()).isEqualTo(new Station("신사역"))
        );
    }

    @DisplayName("구간을 추가한다.")
    @Test
    void addSection() {
        //given
        final Station 잠실역 = new Station("잠실역");

        // when
        this.sections.addSection(호선2, 잠실역, 신사역, new Distance(5));

        // then
        assertThat(sections.getSections().size()).isEqualTo(2);
    }

    @DisplayName("구간을 정렬하고 목록을 확인한다.")
    @Test
    void sortSections() {
        // given
        final Station 강남신사사이역 = new Station("강남신사사이역");

        // when
        sections.addSection(호선2, 강남역, 강남신사사이역, new Distance(3));

        // then
        assertThat(sections.getSortedStations()).containsExactly(강남역, 강남신사사이역, 신사역);
    }

    @DisplayName("구간이 추가된 것이 없는데 역 조회할 경우 예외를 발생시킨다.")
    @Test
    void emptySectionException() {
        assertThatThrownBy(() -> {
            final Sections sections = new Sections();
            sections.getSortedStations();

        }).isInstanceOf(EmptySectionException.class)
                .hasMessageContaining("구간을 찾을 수 없습니다.");
    }

    @DisplayName("두 역을 모두 포함한 경우 예외를 발생시킨다.")
    @Test
    void addSectionBothStationsException() {
        assertThatThrownBy(() -> {
            this.sections.addSection(호선2, 강남역, 신사역, new Distance(5));

        }).isInstanceOf(InvalidDuplicatedSection.class)
        .hasMessageContaining("현재 입력된 역들이 겹치는 구간입니다.");
    }

    @DisplayName("두 역을 모두 포함하지 않는 경우 예외를 발생시킨다.")
    @Test
    void addSectionNoStationsException() {
        assertThatThrownBy(() -> {
            this.sections.addSection(호선2, new Station("새로운 역1"), new Station("새로운 역2"), new Distance(5));

        }).isInstanceOf(NotContainsStationException.class)
        .hasMessageContaining("현재 입력된 역들이 노선에 존재하지 않습니다.");
    }

    @DisplayName("제거하려는 역이 없다면 예외를 발생시킨다.")
    @Test
    void removeSectionNoStationException() {
        assertThatThrownBy(() -> {
            final Station 강남신사사이역 = new Station("강남신사사이역");
            sections.addSection(호선2, 강남역, 강남신사사이역, new Distance(3));

            this.sections.removeSection(호선2, new Station("새로운 역1"));

        }).isInstanceOf(NotContainsStationException.class)
                .hasMessageContaining("현재 입력된 역들이 노선에 존재하지 않습니다.");
    }

    @DisplayName("최수 구간 크키보다 작을 때 제거를 시도하면 예외를 발생시킨다.")
    @Test
    void removeSectionMinimumSectionSizeException() {
        assertThatThrownBy(() -> {
            this.sections.removeSection(호선2, 강남역);

        }).isInstanceOf(MinimumRemovableSectionSizeException.class)
                .hasMessageContaining("삭제할 수 없는 구간 크기 입니다.");
    }
}