package nextstep.subway.line.domain;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
}