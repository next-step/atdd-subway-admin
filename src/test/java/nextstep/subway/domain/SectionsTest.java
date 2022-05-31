package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {
    Station 강남역;
    Station 양재역;
    Station 판교역;
    Station 양재시민의숲역;
    Section section1;
    Section section2;
    List<Section> sections = new ArrayList<>();

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
        section1 = Section.builder(강남역, 양재역, Distance.valueOf(10))
                .build();
        section2 = Section.builder(양재역, 양재시민의숲역, Distance.valueOf(10))
                .build();
        sections.add(section1);
        sections.add(section2);
    }

    @DisplayName("빈 구간들로부터 구간들의 상행 지하철역을 구할때 예외 테스트")
    @Test
    void upStationFromEmptySections() {
        Sections sections = new Sections();
        assertThatIllegalStateException()
                .isThrownBy(sections::upStation)
                .withMessage("지하철 구간이 비어있습니다.");
    }

    @DisplayName("빈 구간들로부터 구간들의 하행 지하철역을 구할때 예외 테스트")
    @Test
    void downStationFromEmptySections() {
        Sections sections = new Sections();
        assertThatIllegalStateException()
                .isThrownBy(sections::downStation)
                .withMessage("지하철 구간이 비어있습니다.");
    }

    @DisplayName("구간들 사이에 새로운 구간 등록 테스트 (상행역 일치)")
    @Test
    void addSectionIfEqualUpStation() {
        Section newSection = Section.builder(양재역, 판교역, Distance.valueOf(5))
                .build();
        Sections sections = Sections.valueOf(this.sections);
        sections.addSection(newSection);
        List<String> upStationNames = sections.sections().stream()
                .map(section -> section.upStation().name())
                .collect(Collectors.toList());
        assertThat(upStationNames).containsOnly("강남역", "양재역", "판교역");
        //assertThat(upStationNames).containsExactly("강남역", "양재역", "판교역");
    }

    @DisplayName("구간들 사이에 새로운 구간 등록 테스트 (하행역 일치)")
    @Test
    void addSectionIfEqualDownStation() {
        Section newSection = Section.builder(판교역, 양재시민의숲역, Distance.valueOf(4))
                .build();
        Sections sections = Sections.valueOf(this.sections);
        sections.addSection(newSection);
        List<String> upStationNames = sections.sections().stream()
                .map(section -> section.upStation().name())
                .collect(Collectors.toList());
        assertThat(upStationNames).containsOnly("강남역", "양재역", "판교역");
        //assertThat(upStationNames).containsExactly("강남역", "양재역", "판교역");
    }
}
