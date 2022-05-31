package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

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
        section1 = Section.builder(강남역, 양재역, Distance.valueOf(10))
                .build();
        section2 = Section.builder(양재역, 양재시민의숲역, Distance.valueOf(10))
                .build();
        sections.add(section1);
        sections.add(section2);
        sections1 = Sections.valueOf(this.sections);
    }

    @DisplayName("구간들 사이에 새로운 구간 등록 테스트 (상행역 일치)")
    @Test
    void addSectionIfEqualUpStation() {
        Section newSection = Section.builder(양재역, 판교역, Distance.valueOf(5))
                .build();
        sections1.addSection(newSection);
        List<String> stationNames = sections1.stations().stream()
                .map(Station::name)
                .collect(Collectors.toList());
        assertThat(stationNames).containsOnly("강남역", "양재역", "판교역", "양재시민의숲역");
    }

    @DisplayName("구간들 사이에 새로운 구간 등록 테스트 (하행역 일치)")
    @Test
    void addSectionIfEqualDownStation() {
        Section newSection = Section.builder(판교역, 양재시민의숲역, Distance.valueOf(4))
                .build();
        sections1.addSection(newSection);
        List<String> stationNames = sections1.stations().stream()
                .map(Station::name)
                .collect(Collectors.toList());
        assertThat(stationNames).containsOnly("강남역", "양재역", "판교역", "양재시민의숲역");
    }

    @DisplayName("구간들 길이(합) 테스트")
    @Test
    void getDistance() {
        assertThat(sections1.distance()).isEqualTo(Distance.valueOf(20));
    }

    @DisplayName("구간들에 포함된 역 정보들 조회 테스트")
    @Test
    void getStations() {
        assertThat(sections1.stations()).containsOnly(강남역, 양재역, 양재시민의숲역);
    }
}
