package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;

public abstract class SectionsTestFixture {
    Station 강남역;
    Station 양재역;
    Station 판교역;
    Station 양재시민의숲역;
    Station 미정역;
    Line 신분당선;
    Section section1;
    Section section2;
    List<Section> sections = new ArrayList<>();
    Sections sections1;

    @BeforeEach
    void setUp() {
        강남역 = Station.builder("강남역")
                .id(1L)
                .build();
        양재역 = Station.builder("양재역")
                .id(2L)
                .build();
        판교역 = Station.builder("판교역")
                .id(3L)
                .build();
        양재시민의숲역 = Station.builder("양재시민의숲역")
                .id(4L)
                .build();
        미정역 = Station.builder("미정역")
                .id(5L)
                .build();
        신분당선 = Line.builder("신분당선", "bg-red-600")
                .id(1L)
                .build();
        section1 = Section.builder(강남역, 양재역, Distance.valueOf(10))
                .build();
        section2 = Section.builder(양재역, 양재시민의숲역, Distance.valueOf(10))
                .build();
        section1.addLine(신분당선);
        section2.addLine(신분당선);
        sections.add(section1);
        sections.add(section2);
        sections1 = Sections.valueOf(this.sections);
    }
}
