package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SectionsTest {
    Station 강남역;
    Station 양재역;
    Station 판교역;
    Station 양재시민의숲역;
    Station 미정역;
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

    @DisplayName("구간들에 있는 하행역이 일치하는 구간 사이에 구간 추가")
    @Test
    void addSectionInside() {
        Section newSection = Section.builder(판교역, 양재시민의숲역, Distance.valueOf(4))
                .build();
        sections1.addSection(newSection);
        assertThat(sections1.stations()).containsOnly(강남역, 양재역, 양재시민의숲역, 판교역);
    }

    @DisplayName("구간들에 하행 종점에 구간 추가")
    @Test
    void addSectionAtDownStation() {
        Section newSection = Section.builder(양재시민의숲역, 판교역, Distance.valueOf(12))
                .build();
        sections1.addSection(newSection);
        assertThat(sections1.stations()).containsOnly(강남역, 양재역, 양재시민의숲역, 판교역);
    }

    @DisplayName("구간들에 있는 하행역이 일치하는 구간 사이에 구간길이가 같거나 큰 구간 추가하면 예외 발생")
    @ParameterizedTest(name = "구간들에 있는 하행역이 일치하는 구간 사이에 {0}의 구간길이가 같거나 큰 구간 추가하면 예외 발생")
    @ValueSource(ints = {10, 12})
    void addSectionInsideByEqualOrLongerDistance(int input) {
        Section newSection = Section.builder(판교역, 양재시민의숲역, Distance.valueOf(input))
                .build();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> sections1.addSection(newSection))
                .withMessage("구간 길이는 0 이하가 될 수 없습니다.");
    }

    @DisplayName("구간들에 구간 등록시 상행역과 하행역이 이미 노선에 모두 등록되어 있는 경우 예외")
    @Test
    void addSectionContainsUpStationAndDownStation() {
        Section newSection = Section.builder(양재역, 양재시민의숲역, Distance.valueOf(3))
                .build();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> sections1.addSection(newSection))
                .withMessage("이미 등록된 구간 요청입니다.");
    }

    @DisplayName("구간들에 구간 등록시 상행역과 하행역 둘 중 하나도 노선에 포함되어있지 않으면 예외 발생")
    @Test
    void addSectionContainsNoneOfUpStationAndDownStation() {
        Section newSection = Section.builder(판교역, 미정역, Distance.valueOf(3))
                .build();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> sections1.addSection(newSection))
                .withMessage("등록을 위해 필요한 상행역과 하행역이 모두 등록되어 있지 않습니다.");
    }
}
