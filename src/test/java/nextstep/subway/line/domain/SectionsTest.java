package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간에 대한 테스트")
class SectionsTest {

    private Line line;

    @BeforeEach
    void setUp() {
        line = LineTest.지하철_1호선_생성됨();
    }

    @DisplayName("지하철 구간의 초기 값을 설정합니다.")
    @Test
    void init() {
        // given
        Section section = Section.of(line, new Station("청량리역"), new Station("신창역"), 100);

        // when
        Sections init = Sections.init(section);

        // then
        assertThat(init).isNotNull();
        assertThat(init.getSections()).containsExactly(section);
    }

    @DisplayName("지하철 구간을 추가합니다.")
    @Test
    void add() {
        // given
        Section section = Section.of(line, new Station("청량리역"), new Station("신창역"), 100);
        Sections sections = Sections.init(section);
        Section newStation = Section.of(line, new Station("당정역"), new Station("금정역"), 10);

        // when
        sections.add(newStation);

        // then
        assertThat(sections.getSections()).contains(newStation);
    }

}
