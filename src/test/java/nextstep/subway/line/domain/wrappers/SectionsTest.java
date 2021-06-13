package nextstep.subway.line.domain.wrappers;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionsTest {

    private List<Section> sections;

    @BeforeEach
    void setUp() {
        sections = new ArrayList<>();
        Station upStation = new Station(1L, "강남역");
        Station downStation = new Station(2L, "양재역");
        sections.add(new Section(upStation, downStation, 10));
    }

    @Test
    @DisplayName("구간 정보 일급 컬렉션 생성")
    void create() {
        assertThat(new Sections(this.sections)).isEqualTo(new Sections(this.sections));
    }


    @Test
    @DisplayName("구간 정보 일급 컬렉션 add")
    void addSection() {
        Sections actual = new Sections(this.sections);
        Station otherUpStation = new Station(2L, "양재역");
        Station otherDownStation = new Station(3L, "시민의숲역");
        Section otherSection = new Section(otherUpStation, otherDownStation, 10);

        actual.addSection(otherSection);

        this.sections.add(otherSection);
        assertThat(actual).isEqualTo(new Sections(this.sections));
    }

    @Test
    @DisplayName("구간 정보 일급 컬렉션 중복 데이터 add")
    void addSection2() {
        Sections actual = new Sections(this.sections);
        Station otherUpStation = new Station(1L, "강남역");
        Station otherDownStation = new Station(2L, "양재역");
        Section otherSection = new Section(otherUpStation, otherDownStation, 10);

        actual.addSection(otherSection);

        assertThat(actual).isEqualTo(new Sections(this.sections));
    }

    @Test
    @DisplayName("구간 정보 포함 여부 확인")
    void contains() {
        Station upStation = new Station(1L, "강남역");
        Station downStation = new Station(2L, "양재역");
        Section section = new Section(upStation, downStation, 10);

        Sections actual = new Sections(this.sections);

        Station otherUpStation = new Station(1L, "강남역");
        Station otherDownStation = new Station(3L, "광교역");
        Section otherSection = new Section(otherUpStation, otherDownStation, 10);

        assertThat(actual.contains(section)).isTrue();
        assertThat(actual.contains(otherSection)).isFalse();
    }

    @Test
    @DisplayName("구간 정보에 포함되어 있는 상, 하행 지하철역 리스트 생성")
    void generateStations() {
        Sections actual = new Sections(this.sections);
        Station otherUpStation = new Station(2L, "양재역");
        Station otherDownStation = new Station(3L, "시민의숲역");
        Section otherSection = new Section(otherUpStation, otherDownStation, 10);
        actual.addSection(otherSection);

        List<Station> stations = actual.generateStations();

        assertThat(stations.size()).isEqualTo(3);
        assertThat(stations).containsExactly(new Station(1L, "강남역"), new Station(2L, "양재역"), new Station(3L, "시민의숲역"));
    }

    @Test
    @DisplayName("구간 정보에 포함되어 있는 상, 하행 지하철역 리스트 생성 구간 하나")
    void generateStations2() {
        Sections actual = new Sections(this.sections);

        List<Station> stations = actual.generateStations();

        assertThat(stations.size()).isEqualTo(2);
        assertThat(stations).containsExactly(new Station(1L, "강남역"), new Station(2L, "양재역"));
    }
}
