package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Sections 일급콜렉션 클래스 관련 테스트")
public class SectionsTest {

    public static Section Section을_두개역과_만든다(String upStationName, String downStationName, int distance) {
        Station upStation = Station.of(upStationName);
        Station downStation = Station.of(downStationName);

        return Section.of(upStation, downStation, distance);
    }

    @Test
    void create() {
        Sections sections = new Sections();

        assertThat(sections.stream().count()).isZero();
    }

    @Test
    void Sections_상행에서_하행으로_Section을_정렬조회한다() {
        Sections sections = new Sections();

        Section section2 = Section을_두개역과_만든다("2번역", "3번역", 5);
        Section section1 = Section을_두개역과_만든다("1번역", "2번역", 5);
        Section section3 = Section을_두개역과_만든다("3번역", "4번역", 5);
        Section section4 = Section을_두개역과_만든다("4번역", "5번역", 5);

        sections.add(section2);
        sections.add(section1);
        sections.add(section3);
        sections.add(section4);

        List<Station> actualStations = sections.stations();
        for (Station actualStation : actualStations) {
            System.out.println(actualStation.getName());
        }
        assertThat(actualStations.get(0).getName()).isEqualTo("1번역");
        assertThat(actualStations.get(1).getName()).isEqualTo("2번역");
        assertThat(actualStations.get(2).getName()).isEqualTo("3번역");
        assertThat(actualStations.get(3).getName()).isEqualTo("4번역");
        assertThat(actualStations.get(4).getName()).isEqualTo("5번역");
    }

    @Test
    void Section을_추가한다() {
        Sections sections = new Sections();

        Section section = Section을_두개역과_만든다("상행선", "하행선", 5);

        sections.add(section);

        assertThat(sections.stream().count()).isOne();
    }

    @Test
    void Section을_중복_추가는_영향없다() {
        Sections sections = new Sections();

        Station upStation = Station.of("상행선");
        Station downStation = Station.of("하행선");
        int distance = 5;
        Section section = Section.of(upStation, downStation, distance);

        sections.add(section);
        sections.add(section);

        assertThat(sections.stream().count()).isOne();
    }

}
