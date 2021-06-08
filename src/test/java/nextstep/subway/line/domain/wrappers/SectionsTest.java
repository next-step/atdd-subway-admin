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
        Station upStation = new Station("강남역");
        Station downStation = new Station("광교역");
        sections.add(new Section(upStation, downStation, 100));
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
        Station otherUpStation = new Station("강남역");
        Station otherDownStation = new Station("양재역");
        Section otherSection = new Section(otherUpStation, otherDownStation, 10);

        actual.addSection(otherSection);

        this.sections.add(otherSection);
        assertThat(actual).isEqualTo(new Sections(this.sections));
    }

    @Test
    @DisplayName("구간 정보 일급 컬렉션 중복 데이터 add")
    void addSection2() {
        Sections actual = new Sections(this.sections);
        Station otherUpStation = new Station("강남역");
        Station otherDownStation = new Station("광교역");
        Section otherSection = new Section(otherUpStation, otherDownStation, 100);

        actual.addSection(otherSection);

        assertThat(actual).isEqualTo(new Sections(this.sections));
    }

    @Test
    @DisplayName("구간 정보 포함 여부 확인")
    void contains() {
        Station upStation = new Station("강남역");
        Station downStation = new Station("광교역");
        Section section = new Section(upStation, downStation, 100);

        Sections actual = new Sections(this.sections);

        Station otherUpStation = new Station("강남역");
        Station otherDownStation = new Station("양재역");
        Section otherSection = new Section(otherUpStation, otherDownStation, 10);

        assertThat(actual.contains(section)).isTrue();
        assertThat(actual.contains(otherSection)).isFalse();
    }
}
