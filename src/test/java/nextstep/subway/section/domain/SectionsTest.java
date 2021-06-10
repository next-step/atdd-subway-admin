package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

/**
 * Sections 클래스의 기능 검증 테스트
 */
public class SectionsTest {

    private Line line;
    private Line blueLine;
    private Station station2;
    private Station station3;
    private Station station1;
    private Station station4;
    private Station station5;
    private List<Station> lineStations;
    private Section section1;
    private Section section2;
    private Section section3;
    private Section section4;
    private Sections sections;

    @BeforeEach
    void setUp() {
        line = new Line("2호선", "green");
        blueLine = new Line("4호선", "blue");
        station2 = new Station("역삼역");
        station3 = new Station("교대역");
        station1 = new Station("강남역");
        station4 = new Station("서초역");
        station5 = new Station("방배역");
        lineStations = Arrays.asList(station2, station3, station1, station4, station5);
        section1 = new Section(station2, station3, 3, line);
        section2 = new Section(station1, station4, 10, line);
        section3 = new Section(station4, station5, 3, line);
        section4 = new Section(station3, station1, 3, line);
        sections = new Sections(Arrays.asList(section1, section2, section3, section4));
    }

    @Test
    @DisplayName("상행역에서 하행역으로 정렬된 역 목록 반환")
    void getStationResponses() {
        // when
        List<Station> createStations = sections.getSortedStations();

        // then
        List<StationResponse> targetStationResponses = lineStations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
        List<StationResponse> resultStationResponses = createStations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
        assertThat(Arrays.equals(resultStationResponses.toArray(), targetStationResponses.toArray())).isTrue();
    }

    @Test
    @DisplayName("일급 컬렉션 초기화 유효성 검증")
    void sections_is_empty_exception() {
        // then
        assertThatThrownBy(() -> new Sections(new ArrayList<>()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간 목록은 하나 이상의 구간으로 구성되어야 합니다.");
    }

    @Test
    @DisplayName("신규 구간을 가장 앞에 추가")
    void appendSection_to_front() {
        // given
        Section section = new Section(station1, station2, 7, line);
        Section targetSection = new Section(station3, station1, 6, line);
        Sections sections = new Sections(Arrays.asList(section));

        // when
        sections.appendNewSection(targetSection);

        // then
        List<Station> stations = Arrays.asList(station3, station1, station2);
        assertThat(Arrays.equals(sections.getSortedStations().toArray(), stations.toArray())).isTrue();
    }

    @Test
    @DisplayName("신규 구간을 가장 뒤에 추가")
    void appendSection_at_back() {
        // given
        Section section = new Section(station1, station2, 7, line);
        Section targetSection = new Section(station2, station4, 6, line);
        Sections sections = new Sections(Arrays.asList(section));

        // when
        sections.appendNewSection(targetSection);

        // then
        List<Station> stations = Arrays.asList(station1, station2, station4);
        assertThat(Arrays.equals(sections.getSortedStations().toArray(), stations.toArray())).isTrue();
    }

    @Test
    @DisplayName("이미 존재하는 상,하행역 추가 시 예외처리1")
    void upDownStation_already_exist_exception() {
        // given
        Section section = new Section(station1, station2, 7, line);
        Section targetSection = new Section(station1, station2, 6, line);
        Sections sections = new Sections(Arrays.asList(section));

        // then
        assertThatThrownBy(() -> sections.appendNewSection(targetSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역, 하행역이 이미 존재합니다.");
    }

    @Test
    @DisplayName("이미 존재하는 상,하행역 추가 시 예외처리2")
    void upDownStation_already_exist_exception1() {
        // given
        Section section = new Section(station1, station2, 7, line);
        Section targetSection = new Section(station2, station1, 6, line);
        Sections sections = new Sections(Arrays.asList(section));

        // then
        assertThatThrownBy(() -> sections.appendNewSection(targetSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역, 하행역이 이미 존재합니다.");
    }

    @Test
    @DisplayName("한 역을 기점으로 같은 길이를 가지는 구간이 존재할 경우 예외처리")
    void sameDistance_exception() {
        // given
        Section section = new Section(station1, station2, 7, new Line("2", "3"));
        Section upStationBase = new Section(station1, station3, 7, new Line("2", "3"));
        Section downStationBase = new Section(station5, station2, 7, new Line("2", "3"));
        Sections sections = new Sections(Arrays.asList(section));

        // then
        assertAll(
                () -> assertThatThrownBy(() -> sections.appendNewSection(upStationBase))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("같은 길이의 구간이 존재합니다."),
                () -> assertThatThrownBy(() -> sections.appendNewSection(downStationBase))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("같은 길이의 구간이 존재합니다.")
        );
    }

    @Test
    @DisplayName("상,하행 역 중 기존 노선에 등록된 역이 하나도 없을 경우 예외처리")
    void notExists_anyStations() {
        // given
        Section section = new Section(station1, station2, 7, line);
        Section targetStation = new Section(station3, station4, 7, line);
        Sections sections = new Sections(Arrays.asList(section));

        // then
        assertThatThrownBy(() -> sections.appendNewSection(targetStation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상,하행역 모두 기존 노선에 포함된 역이 아닙니다.");
    }

    @Test
    @DisplayName("상행역이 기존 노선에 존재하고 구간의 길이가 기존 구간보다 길 경우")
    void append_newSection1() {
        // given
        Section section = new Section(station1, station2, 4, blueLine);
        Section targetStation = new Section(station1, station4, 7, blueLine);
        Sections sections = new Sections(Arrays.asList(section));

        // then
        Section appendedSection = sections.appendNewSection(targetStation);

        assertAll(
                () -> assertThat(sections.getSections().size()).isEqualTo(2),
                () -> assertThat(blueLine.getSections().size()).isEqualTo(2),
                () -> assertThat(appendedSection).isNotNull(),
                () -> assertThat(appendedSection.getUpStation()).isSameAs(station2),
                () -> assertThat(appendedSection.getDistance()).isEqualTo(3)
        );
    }

    @Test
    @DisplayName("상행역이 기존 노선에 존재하고 구간의 길이가 기존 구간보다 짧을 경우")
    void append_newSection2() {
        // given
        Section baseSection = new Section(station1, station2, 7, blueLine);
        Section targetStation = new Section(station1, station4, 4, blueLine);
        Sections sections = new Sections(Arrays.asList(baseSection));

        // then
        Section appendedSection = sections.appendNewSection(targetStation);

        assertAll(
                () -> assertThat(sections.getSections().size()).isEqualTo(2),
                () -> assertThat(blueLine.getSections().size()).isEqualTo(2),
                () -> assertThat(appendedSection).isNotNull(),
                () -> assertThat(appendedSection.getUpStation()).isSameAs(station1),
                () -> assertThat(appendedSection.getDownStation()).isSameAs(station4),
                () -> assertThat(appendedSection.getDistance()).isEqualTo(4),
                () -> assertThat(baseSection.getUpStation()).isSameAs(station4),
                () -> assertThat(baseSection.getDistance()).isEqualTo(3)
        );
    }

    @Test
    @DisplayName("하행역이 기존 조선에 존재하고 구간의 길이가 기존 구간보다 길 경우")
    void append_newSection3() {
        // given
        Section baseSection = new Section(station2, station4, 4, blueLine);
        Section targetStation = new Section(station1, station4, 7, blueLine);
        Sections sections = new Sections(Arrays.asList(baseSection));

        // then
        Section appendedSection = sections.appendNewSection(targetStation);

        assertAll(
                () -> assertThat(sections.getSections().size()).isEqualTo(2),
                () -> assertThat(blueLine.getSections().size()).isEqualTo(2),
                () -> assertThat(appendedSection).isNotNull(),
                () -> assertThat(appendedSection.getUpStation()).isSameAs(station1),
                () -> assertThat(appendedSection.getDownStation()).isSameAs(station2),
                () -> assertThat(appendedSection.getDistance()).isEqualTo(3)
        );
    }

    @Test
    @DisplayName("하행역이 기존 조선에 존재하고 구간의 길이가 기존 구간보다 짧은 경우")
    void append_newSection4() {
        // given
        Section baseSection = new Section(station2, station4, 7, blueLine);
        Section targetStation = new Section(station1, station4, 4, blueLine);
        Sections sections = new Sections(Arrays.asList(baseSection));

        // then
        Section appendedSection = sections.appendNewSection(targetStation);

        assertAll(
                () -> assertThat(sections.getSections().size()).isEqualTo(2),
                () -> assertThat(blueLine.getSections().size()).isEqualTo(2),
                () -> assertThat(appendedSection).isNotNull(),
                () -> assertThat(appendedSection.getUpStation()).isSameAs(station1),
                () -> assertThat(appendedSection.getDownStation()).isSameAs(station4),
                () -> assertThat(appendedSection.getDistance()).isEqualTo(4),
                () -> assertThat(baseSection.getDownStation()).isSameAs(station1),
                () -> assertThat(baseSection.getDistance()).isEqualTo(3)
        );
    }

    @Test
    @DisplayName("한 역을 기점으로 구간들의 길이가 신규구간의 길이와 같을 경우 예외처리")
    void totalDistance_same_exception() {
        // given
        Section section1 = new Section(station1, station2, 2, blueLine);
        Section section2 = new Section(station2, station3, 3, blueLine);
        Section section3 = new Section(station3, station4, 4, blueLine);
        Section targetStation1 = new Section(station1, station5, 2, blueLine);
        Section targetStation2 = new Section(station5, station4, 9, blueLine);
        Sections sections = new Sections(Arrays.asList(section1, section2, section3));

        // then
        assertAll(
                () -> assertThatThrownBy(() -> sections.appendNewSection(targetStation1))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("같은 길이의 구간이 존재합니다."),
                () -> assertThatThrownBy(() -> sections.appendNewSection(targetStation2))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("같은 길이의 구간이 존재합니다.")
        );
    }

    @Test
    @DisplayName("상행역 기준으로 신규구간의 길이가 기존 구간보다 짧고 그 사이에 여러 구간이 존재 할 경우")
    void append_newSection5() {
        // given
        Section section1 = new Section(station1, station2, 2, blueLine);
        Section section2 = new Section(station2, station3, 3, blueLine);
        Section section3 = new Section(station3, station4, 4, blueLine);
        Section targetStation = new Section(station1, station5, 7, blueLine);
        Sections sections = new Sections(Arrays.asList(section1, section2, section3));

        // when
        Section appendedSection = sections.appendNewSection(targetStation);

        // then
        assertAll(
                () -> assertThat(sections.getSections().size()).isEqualTo(4),
                () -> assertThat(blueLine.getSections().size()).isEqualTo(4),
                () -> assertThat(appendedSection).isNotNull(),
                () -> assertThat(appendedSection.getUpStation()).isSameAs(station3),
                () -> assertThat(appendedSection.getDistance()).isEqualTo(2),
                () -> assertThat(section1.getUpStation()).isSameAs(station1),
                () -> assertThat(section1.getDownStation()).isSameAs(station2),
                () -> assertThat(section1.getDistance()).isEqualTo(2),
                () -> assertThat(section2.getUpStation()).isSameAs(station2),
                () -> assertThat(section2.getDownStation()).isSameAs(station3),
                () -> assertThat(section2.getDistance()).isEqualTo(3),
                () -> assertThat(section3.getUpStation()).isSameAs(station5),
                () -> assertThat(section3.getDownStation()).isSameAs(station4),
                () -> assertThat(section3.getDistance()).isEqualTo(2)
        );
    }

    @Test
    @DisplayName("상행역 기준으로 신규구간의 길이가 기존 구간보다 길고 그 사이에 여러 구간이 존재 할 경우")
    void append_newSection6() {
        // given
        Section section1 = new Section(station1, station2, 2, blueLine);
        Section section2 = new Section(station2, station3, 3, blueLine);
        Section section3 = new Section(station3, station4, 4, blueLine);
        Section targetStation = new Section(station1, station5, 10, blueLine);
        Sections sections = new Sections(Arrays.asList(section1, section2, section3));

        // when
        Section appendedSection = sections.appendNewSection(targetStation);

        // then
        assertAll(
                () -> assertThat(sections.getSections().size()).isEqualTo(4),
                () -> assertThat(blueLine.getSections().size()).isEqualTo(4),
                () -> assertThat(appendedSection).isNotNull(),
                () -> assertThat(appendedSection.getUpStation()).isSameAs(station4),
                () -> assertThat(appendedSection.getDownStation()).isSameAs(station5),
                () -> assertThat(appendedSection.getDistance()).isEqualTo(1),
                () -> assertThat(section1.getUpStation()).isSameAs(station1),
                () -> assertThat(section1.getDownStation()).isSameAs(station2),
                () -> assertThat(section1.getDistance()).isEqualTo(2),
                () -> assertThat(section2.getUpStation()).isSameAs(station2),
                () -> assertThat(section2.getDownStation()).isSameAs(station3),
                () -> assertThat(section2.getDistance()).isEqualTo(3),
                () -> assertThat(section3.getUpStation()).isSameAs(station3),
                () -> assertThat(section3.getDownStation()).isSameAs(station4),
                () -> assertThat(section3.getDistance()).isEqualTo(4)
        );
    }

    @Test
    @DisplayName("하행역 기준으로 신규구간의 길이가 기존 구간보다 짧고 그 사이에 여러 구간이 존재 할 경우")
    void append_newSection7() {
        // given
        Section section1 = new Section(station1, station2, 2, blueLine);
        Section section2 = new Section(station2, station3, 3, blueLine);
        Section section3 = new Section(station3, station4, 4, blueLine);
        Section targetStation = new Section(station5, station4, 8, blueLine);
        Sections sections = new Sections(Arrays.asList(section1, section2, section3));

        // when
        Section appendedSection = sections.appendNewSection(targetStation);

        // then
        assertAll(
                () -> assertThat(sections.getSections().size()).isEqualTo(4),
                () -> assertThat(blueLine.getSections().size()).isEqualTo(4),
                () -> assertThat(appendedSection).isNotNull(),
                () -> assertThat(appendedSection.getUpStation()).isSameAs(station5),
                () -> assertThat(appendedSection.getDownStation()).isSameAs(station2),
                () -> assertThat(appendedSection.getDistance()).isEqualTo(1),
                () -> assertThat(section1.getUpStation()).isSameAs(station1),
                () -> assertThat(section1.getDownStation()).isSameAs(station5),
                () -> assertThat(section1.getDistance()).isEqualTo(1),
                () -> assertThat(section2.getUpStation()).isSameAs(station2),
                () -> assertThat(section2.getDownStation()).isSameAs(station3),
                () -> assertThat(section2.getDistance()).isEqualTo(3),
                () -> assertThat(section3.getUpStation()).isSameAs(station3),
                () -> assertThat(section3.getDownStation()).isSameAs(station4),
                () -> assertThat(section3.getDistance()).isEqualTo(4)
        );
    }

    @Test
    @DisplayName("하행역 기준으로 신규구간의 길이가 기존 구간보다 길고 그 사이에 여러 구간이 존재 할 경우")
    void append_newSection8() {
        // given
        Section section1 = new Section(station1, station2, 2, blueLine);
        Section section2 = new Section(station2, station3, 3, blueLine);
        Section section3 = new Section(station3, station4, 4, blueLine);
        Section targetStation = new Section(station5, station4, 11, blueLine);
        Sections sections = new Sections(Arrays.asList(section1, section2, section3));

        // when
        Section appendedSection = sections.appendNewSection(targetStation);

        // then
        assertAll(
                () -> assertThat(sections.getSections().size()).isEqualTo(4),
                () -> assertThat(blueLine.getSections().size()).isEqualTo(4),
                () -> assertThat(appendedSection).isNotNull(),
                () -> assertThat(appendedSection.getUpStation()).isSameAs(station5),
                () -> assertThat(appendedSection.getDownStation()).isSameAs(station1),
                () -> assertThat(appendedSection.getDistance()).isEqualTo(2),
                () -> assertThat(section1.getUpStation()).isSameAs(station1),
                () -> assertThat(section1.getDownStation()).isSameAs(station2),
                () -> assertThat(section1.getDistance()).isEqualTo(2),
                () -> assertThat(section2.getUpStation()).isSameAs(station2),
                () -> assertThat(section2.getDownStation()).isSameAs(station3),
                () -> assertThat(section2.getDistance()).isEqualTo(3),
                () -> assertThat(section3.getUpStation()).isSameAs(station3),
                () -> assertThat(section3.getDownStation()).isSameAs(station4),
                () -> assertThat(section3.getDistance()).isEqualTo(4)
        );
    }
}
