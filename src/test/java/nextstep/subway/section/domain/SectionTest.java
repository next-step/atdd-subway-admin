package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Section 클래스 관련 테스트")
public class SectionTest {

    @BeforeEach
    void setup() {

    }

    @Test
    void create() {
        Station upStation = Station.of("상행선");
        Station downStation = Station.of("하행선");
        int distance = 5;

        Section section = Section.of(upStation, downStation, distance);

        assertThat(section).isNotNull();
    }

    @Test
    void Section의_Station들을_확인한다() {
        Station upStation = Station.of("상행선");
        Station downStation = Station.of("하행선");
        int distance = 5;

        Section section = Section.of(upStation, downStation, distance);

        Station expectUpStation = section.upStation();
        Station expectDownStation = section.downStation();

        assertThat(expectUpStation.getName()).isEqualTo(upStation.getName());
        assertThat(expectDownStation.getName()).isEqualTo(downStation.getName());
    }

    @Test
    void Line을_생성시_Section이_추가된다() {
        Station upStation = Station.of("상행선");
        Station downStation = Station.of("하행선");
        int distance = 5;

        Section section = Section.of(upStation, downStation, distance);
        Line line = Line.of("1호선", "파란색", section);

        assertThat(section.getLine()).isEqualTo(line);
    }

    @Test
    void Section에_Line을_지정한다() {
        Station upStation = Station.of("상행선");
        Station downStation = Station.of("하행선");
        int distance = 5;

        Section section = Section.of(upStation, downStation, distance);
        Line line = Line.of("1호선", "파란색");

        section.setLine(line);

        assertThat(section.getLine()).isEqualTo(line);
    }

}
