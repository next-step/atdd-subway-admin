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
        assertThat(line2.stations()).containsExactly(station1, station2, station4, station3, station6, station5);
    }

    @Test
    void Sections_의_사이사이에_여러개를_추가한다() {
        Sections sections = new Sections();

        Section section2 = Section을_두개역과_만든다("1번역", "5번역", 100);
        Section section1 = Section을_두개역과_만든다("1번역", "2번역", 5);
        Section section3 = Section을_두개역과_만든다("4번역", "5번역", 5);
        Section section4 = Section을_두개역과_만든다("3번역", "4번역", 5);

        line2.addSection(section2);
        List<Station> actualStations = line2.stations();
        List<Section> actualSections = line2.sections().get();
        for (Station actualStation : actualStations) {
            System.out.print(actualStation.getName() + " ");
        } System.out.println("\n========================");
        for (Section actualSection : actualSections) {
            System.out.print(actualSection.upStationName() + " " + actualSection.downStationName() + "\t");
        }System.out.println("\n========================");
        System.out.println("\n========================");
        line2.addSection(section1);
        actualStations = line2.stations();
        for (Station actualStation : actualStations) {
            System.out.print(actualStation.getName() + " ");
        } System.out.println("\n========================");
        for (Section actualSection : actualSections) {
            System.out.print(actualSection.upStationName() + " " + actualSection.downStationName() + "\t");
        }System.out.println("\n========================");
        System.out.println("\n========================");
        line2.addSection(section3);
        actualStations = line2.stations();
        for (Station actualStation : actualStations) {
            System.out.print(actualStation.getName() + " ");
        } System.out.println("\n========================");
        for (Section actualSection : actualSections) {
            System.out.print(actualSection.upStationName() + " " + actualSection.downStationName() + "\t");
        }System.out.println("\n========================");
        System.out.println("\n========================");
        line2.addSection(section4);

        actualStations = line2.stations();
        for (Station actualStation : actualStations) {
            System.out.print(actualStation.getName() + " ");
        } System.out.println("========================");
        for (Section actualSection : actualSections) {
            System.out.print(actualSection.upStationName() + " " + actualSection.downStationName() + "\t");
        }System.out.println("\n========================");
        System.out.println("\n========================");

        assertThat(actualStations.get(0).getName()).isEqualTo("1번역");
        assertThat(actualStations.get(1).getName()).isEqualTo("2번역");
        assertThat(actualStations.get(2).getName()).isEqualTo("3번역");
        assertThat(actualStations.get(3).getName()).isEqualTo("4번역");
        assertThat(actualStations.get(4).getName()).isEqualTo("5번역");
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
