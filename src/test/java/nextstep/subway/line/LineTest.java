package nextstep.subway.line;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Line 클래스 관련 테스트")
public class LineTest {

    private Line line2;
    private Line line3;

    @BeforeEach
    void setup() {
        String line2Name = "2호선";
        String line2Color = "그린";
        line2 = Line.of(line2Name, line2Color);

        String line3Name = "3호선";
        String line3Color = "노란색";
        line3 = Line.of(line3Name, line3Color);
    }

    @Test
    void Line내의_모든_Station_상행to하행_순서로_조회() {
        Station station1 = Station.of("역1");
        Station station2 = Station.of("역2");
        Station station3 = Station.of("역3");
        Station station4 = Station.of("역4");
        Station station5 = Station.of("역5");
        Station station6 = Station.of("역6");
        int distance = 5;

        // 1 - 2 - 4 - 3 - 6 - 5
        Section section1 = Section.of(station1, station2, distance);
        Section section2 = Section.of(station2, station4, distance);
        Section section3 = Section.of(station4, station3, distance);
        Section section4 = Section.of(station3, station6, distance);
        Section section5 = Section.of(station6, station5, distance);

        line2.addSection(section1);
        line2.addSection(section2);
        line2.addSection(section3);
        line2.addSection(section4);
        line2.addSection(section5);

        // expect 1 - 2 - 4 - 3 - 6 - 5
        assertThat(line2.getOrderedStations()).containsExactly(station1, station2, station4, station3, station6, station5);
    }

    @Test
    void Section을_추가() {
        Station upStation = Station.of("상행역");
        Station downStation = Station.of("하행역");
        int distance = 5;
        Section section = Section.of(upStation, downStation, distance);

        line2.addSection(section);

        assertThat(line2.getSections().stream().count()).isOne();
        assertThat(line2.getSections().stream().anyMatch(it -> it.equals(section))).isTrue();
    }

    @Test
    void update() {
        line2.update(line3);

        assertThat(line2.getName()).isEqualTo(line3.getName());
        assertThat(line2.getColor()).isEqualTo(line3.getColor());
    }

    @Test
    void create() {
        String name = "2호선";
        String color = "그린";
        Line line = Line.of(name, color);

        assertThat(line.getName()).isEqualTo(name);
        assertThat(line.getColor()).isEqualTo(color);
    }

}
