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

        Section section4 = Section을_두개역과_만든다("4번역", "5번역", 5);
        Section section2 = Section을_두개역과_만든다("2번역", "3번역", 5);
        Section section1 = Section을_두개역과_만든다("1번역", "2번역", 5);
        Section section3 = Section을_두개역과_만든다("3번역", "4번역", 5);

        sections.add(section4);
        sections.add(section2);
        sections.add(section1);
        sections.add(section3);

        List<Section> actualSections = sections.orderedSections().get();
        assertThat(actualSections.get(0).upStationName()).isEqualTo("1번역");
        assertThat(actualSections.get(1).upStationName()).isEqualTo("2번역");
        assertThat(actualSections.get(2).upStationName()).isEqualTo("3번역");
        assertThat(actualSections.get(3).upStationName()).isEqualTo("4번역");
        assertThat(actualSections.get(0).downStationName()).isEqualTo("2번역");
        assertThat(actualSections.get(1).downStationName()).isEqualTo("3번역");
        assertThat(actualSections.get(2).downStationName()).isEqualTo("4번역");
        assertThat(actualSections.get(3).downStationName()).isEqualTo("5번역");
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
