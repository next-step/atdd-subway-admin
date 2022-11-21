package nextstep.subway.section;

import nextstep.subway.station.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 목록 도메인 테스트")
class SectionsTest {

    private Station 강남역 = new Station("강남역");
    private Station 양재역 = new Station("양재역");
    private Sections sections;

    @BeforeEach
    void init() {
        sections = new Sections();
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        Section 강남_양재_구간 = new Section(강남역, 양재역, new Distance(10L));
        sections.add(강남_양재_구간);
    }

    @DisplayName("구간이 등록된 경우 역 정보 목록 조회")
    @Test
    void getStations() {
        Station 판교역 = new Station("신논현역");
        Section 양재_판교_구간 = new Section(양재역, 판교역, new Distance(10L));
        sections.add(양재_판교_구간);

        assertThat(sections.getStations().size()).isEqualTo(3);
    }

    @DisplayName("구간이 등록되지 않은 경우 역 정보 목록 조회")
    @Test
    void getEmptyStations() {
        Sections sections = new Sections();
        assertThat(sections.getStations().size()).isEqualTo(0);
    }

    @DisplayName("새로운 상행 종점 등록")
    @Test
    void registerUpStation() {
        Station 신사역 = new Station("신사역");
        Section 신사_강남_구간 = new Section(신사역, 강남역, new Distance(10L));
        sections.add(신사_강남_구간);

        List<Map<String, Object>> stations = sections.getStations();
        List<Object> stationNames = stations.stream()
                .map(station -> station.get("name"))
                .collect(Collectors.toList());
        assertThat(stationNames).containsExactly("신사역", "강남역", "양재역");
    }

    @DisplayName("새로운 하행 종점 등록")
    @Test
    void registerDownStation() {
        Station 판교역 = new Station("판교역");
        Section 양재_판교_구간 = new Section(양재역, 판교역, new Distance(10L));
        sections.add(양재_판교_구간);

        List<Map<String, Object>> stations = sections.getStations();
        List<Object> stationNames = stations.stream()
                .map(station -> station.get("name"))
                .collect(Collectors.toList());
        assertThat(stationNames).containsExactly("강남역", "양재역", "판교역");
    }
}