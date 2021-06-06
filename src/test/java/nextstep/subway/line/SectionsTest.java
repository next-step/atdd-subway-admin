package nextstep.subway.line;

import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.TreeSet;

public class SectionsTest {

    @Test
    void name() {
        Sections sections = new Sections();
        Station 강남역 = new Station("강남역");
        Station 양재역 = new Station("양재역");
        Station 수서역 = new Station("수서역");
        Station 일원역 = new Station("일원역");

        sections.add(new Section(강남역, 양재역, 10));
        sections.add(new Section(양재역, 수서역, 15));
        //sections.add(new Section(양재역, 일원역, 15));

        sections.stations().forEach(station -> {
            System.out.println(station.getName());
        });

        //강남역 - 양재역 - 수서역

//        Section section = sections.getSections().ceiling(new Section(양재역, 일원역, 10));
//
//        System.out.println(section.getUpStation().getName());
//        System.out.println(section.getDownStation().getName());

        Section section1 = sections.getSections().higher(new Section(양재역, 일원역, 10));

        System.out.println(section1.getUpStation().getName());
        System.out.println(section1.getDownStation().getName());

        Section section2 = sections.getSections().lower(new Section(양재역, 일원역, 10));

        System.out.println(section2.getUpStation().getName());
        System.out.println(section2.getDownStation().getName());
    }
}
