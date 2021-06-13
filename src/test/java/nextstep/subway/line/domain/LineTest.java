package nextstep.subway.line.domain;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.section.domain.SectionsTest.Section을_두개역과_만든다;
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
    void Line_에서_Station_제거했을_때_구간별_길이의_변화확인() {
        Section section2 = Section을_두개역과_만든다("1번역", "5번역", 100);
        Section section1 = Section을_두개역과_만든다("1번역", "2번역", 5);
        Section section3 = Section을_두개역과_만든다("4번역", "5번역", 5);
        Section section4 = Section을_두개역과_만든다("3번역", "4번역", 5);

        line2.addSection(section2); // 100              1 - 5
        line2.addSection(section1); // 5, 95            1 - 2 - 5
        line2.addSection(section3); // 5, 90, 5         1 - 2 - 4 - 5
        line2.addSection(section4); // 5, 85, 5, 5      1 - 2 - 3 - 4 - 5

        line2.delete(Station.of("3번역"));    // 5, 90, 5     1 - 2 - 4 - 5
        assertThat(line2.sections().get().get(1).distance()).isEqualTo(90);
        line2.delete(Station.of("1번역"));    // 90, 5     2 - 4 - 5
        assertThat(line2.sections().get().get(0).distance()).isEqualTo(90);
    }

    @Test
    void Line_에_Section_넣었을_때_구간별_길이의_변화확인() {
        Section section2 = Section을_두개역과_만든다("1번역", "5번역", 100);
        Section section1 = Section을_두개역과_만든다("1번역", "2번역", 5);
        Section section3 = Section을_두개역과_만든다("4번역", "5번역", 5);
        Section section4 = Section을_두개역과_만든다("3번역", "4번역", 5);

        line2.addSection(section2); // 100              1 - 5
        line2.addSection(section1); // 5, 95            1 - 2 - 5
        line2.addSection(section3); // 5, 90, 5         1 - 2 - 4 - 5
        line2.addSection(section4); // 5, 85, 5, 5      1 - 2 - 3 - 4 - 5

        List<Section> actualSections = line2.sections().get();
        assertThat(actualSections.get(0).distance()).isEqualTo(5);
        assertThat(actualSections.get(1).distance()).isEqualTo(85);
        assertThat(actualSections.get(2).distance()).isEqualTo(5);
        assertThat(actualSections.get(3).distance()).isEqualTo(5);
    }

    @DisplayName("무규칙하게 넣은 구간들이 정렬되며 들어감을 확인한다.")
    @Test
    void Line내의_모든_Station_상행to하행_순서로_조회() {
        // 1 - 2 - 4 - 3 - 6 - 5
        Section section2 = Section을_두개역과_만든다("1번역", "2번역", 5);
        Section section1 = Section을_두개역과_만든다("2번역", "4번역", 5);
        Section section3 = Section을_두개역과_만든다("3번역", "4번역", 3);
        Section section4 = Section을_두개역과_만든다("4번역", "6번역", 10);
        Section section5 = Section을_두개역과_만든다("5번역", "6번역", 6);

        line2.addSection(section1);
        line2.addSection(section2);
        line2.addSection(section3);
        line2.addSection(section4);
        line2.addSection(section5);

        List<Section> actualSections = line2.sections().get();

        assertThat(line2.sections().get()).containsExactly(section2, section1, section3, section4, section5);
    }

    @DisplayName("1~ 5 사이에, 1~ 2, 4~ 5, 3~ 4 순서대로 추가해서 정렬없이 1-2-3-4-5 가 나오는 것을 확인한다.")
    @Test
    void Line_의_Section_을_사이사이_넣으면서_바로_정렬되는_것_역이름으로_확인_테스트() {
        Sections sections = new Sections();

        Section section2 = Section을_두개역과_만든다("1번역", "5번역", 100);
        Section section1 = Section을_두개역과_만든다("1번역", "2번역", 5);
        Section section3 = Section을_두개역과_만든다("4번역", "5번역", 5);
        Section section4 = Section을_두개역과_만든다("3번역", "4번역", 5);

        line2.addSection(section2);
        line2.addSection(section1);
        line2.addSection(section3);
        line2.addSection(section4);

        List<Section> actualSections = line2.sections().get();
        assertThat(actualSections.get(0).upStationName()).isEqualTo("1번역");
        assertThat(actualSections.get(0).downStationName()).isEqualTo("2번역");
        assertThat(actualSections.get(1).upStationName()).isEqualTo("2번역");
        assertThat(actualSections.get(1).downStationName()).isEqualTo("3번역");
        assertThat(actualSections.get(2).upStationName()).isEqualTo("3번역");
        assertThat(actualSections.get(2).downStationName()).isEqualTo("4번역");
        assertThat(actualSections.get(3).upStationName()).isEqualTo("4번역");
        assertThat(actualSections.get(3).downStationName()).isEqualTo("5번역");
    }

    @Test
    void Section을_추가() {
        Station upStation = Station.of("상행역");
        Station downStation = Station.of("하행역");
        int distance = 5;
        Section section = Section.of(upStation, downStation, distance);

        line2.addSection(section);

        assertThat(line2.sections().stream().count()).isOne();
        assertThat(line2.sections().stream().anyMatch(it -> it.equals(section))).isTrue();
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
