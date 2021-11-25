package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("구간 도메인 관련 기능")
class SectionTest {

    private Station upStation;
    private Station downStation;
    private Line line;
    private Section section;

    @BeforeEach
    void setUp() {
        upStation = new Station("강남역");
        downStation = new Station("역삼역");
        line = new Line("2호선", "green", upStation, downStation, 9);
        section = Section.of(line, upStation, downStation, 9);
    }

    @DisplayName("구간을 생성한다.")
    @Test
    void createSection() {
        // then
        assertAll(
                () -> assertThat(section.getLine()).isEqualTo(line),
                () -> assertThat(section.getUpStation()).isEqualTo(upStation),
                () -> assertThat(section.getDownStation()).isEqualTo(downStation),
                () -> assertThat(section.getDistance()).isEqualTo(9)
        );
    }

    @DisplayName("구간 노선을 변경한다.")
    @Test
    void updateSectionWithLine() {
        // when
        final Line newLine = new Line("3호선", "orange");
        section.changeLine(newLine);

        // then
        assertAll(
                () -> assertThat(section.getLine()).isEqualTo(newLine)
        );
    }
}