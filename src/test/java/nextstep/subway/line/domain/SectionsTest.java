package nextstep.subway.line.domain;

import nextstep.subway.common.exception.InvalidDuplicatedSection;
import nextstep.subway.common.exception.NotContainsStationException;
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
    private Station upStation;
    private Station downStation;
    private Line line;

    @BeforeEach
    void setUp() {
        upStation = new Station("강남역");
        downStation = new Station("신사역");
        line = Line.of("2호선", "green", upStation, downStation, 9);
        sections = new Sections();
        sections.getSections().add(Section.of(line, upStation, downStation, 9));
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
        // when
        this.sections.addSection(line, new Station("잠실역"), downStation, 5);

        // then
        assertThat(sections.getSections().size()).isEqualTo(2);
    }

    @DisplayName("두 역을 모두 포함한 경우 예외를 발생시킨다.")
    @Test
    void addSectionBothStationsException() {
        assertThatThrownBy(() -> {
            this.sections.addSection(line, upStation, downStation, 5);
        }).isInstanceOf(InvalidDuplicatedSection.class)
        .hasMessageContaining("현재 입력된 역들이 겹치는 구간입니다.");
    }

    @DisplayName("두 역을 모두 포함하지 않는 경우 예외를 발생시킨다.")
    @Test
    void addSectionNoStationsException() {
        assertThatThrownBy(() -> {
            this.sections.addSection(line, new Station("새로운 역1"), new Station("새로운 역2"), 5);
        }).isInstanceOf(NotContainsStationException.class)
        .hasMessageContaining("현재 입력된 역들이 노선에 존재하지 않습니다.");
    }
}