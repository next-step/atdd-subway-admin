package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import nextstep.subway.line.exception.SectionAlreadyExistInTheLineException;
import nextstep.subway.line.exception.SectionDistanceExceededException;
import nextstep.subway.line.exception.StationsNotExistInTheLine;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    public static final Sections SECTIONS = Sections
        .of(SectionTest.SECTION_2, SectionTest.SECTION_1);
    private static final String SECTION_DISTANCE_EXCEEDED_EXCEPTION = "역 사이에 추가하려는 구간의 거리는 원래 구간 거리보다 작아야 합니다.";
    private static final String SECTION_ALREADY_EXIST_IN_THE_LINE_EXCEPTION = "등록하려는 구간이 이미 노선에 존재합니다.";
    private static final String STATIONS_NOT_EXIST_IN_THE_LINE_EXCEPTION = "상행역과 하행역 둘중 하나는 노선에 존재해야 합니다.";


    @DisplayName("구간을 생성한다.")
    @Test
    void create_sections() {
        assertThat(SECTIONS.getSections().get(0).equals(SectionTest.SECTION_2));
        assertThat(SECTIONS.getSections().get(1).equals(SectionTest.SECTION_1));
    }

    @DisplayName("구간을 순서대로 조회한다.")
    @Test
    void get_ordered_section() {
        List<Section> orderedSections = SECTIONS.orderedSections();
        assertThat(orderedSections.get(0)).isEqualTo(SectionTest.SECTION_1);
        assertThat(orderedSections.get(1)).isEqualTo(SectionTest.SECTION_2);
    }

    @DisplayName("순서대로 정렬된 역 목록을 조회한다.")
    @Test
    void get_ordered_station() {
        List<Station> orderedStation = SECTIONS.stations();
        assertThat(orderedStation.get(0)).isEqualTo(SectionTest.SECTION_1.getUpStation());
        assertThat(orderedStation.get(1)).isEqualTo(SectionTest.SECTION_2.getUpStation());
        assertThat(orderedStation.get(2)).isEqualTo(SectionTest.SECTION_2.getDownStation());
    }

    @DisplayName("구간 사이에 역을 추가한다.")
    @Test
    void add_section_in_the_middle() {
        //given
        int distance = 3;
        int originalDistance = SectionTest.SECTION_2.getDistance();
        Section section = Section
            .of(LineTest.LINE_2, StationTest.STATION_4, StationTest.STATION_1, distance);

        //when
        SECTIONS.addSection(section);
        List<Section> orderedSections = SECTIONS.orderedSections();

        //then
        assertThat(SECTIONS.getSections().size()).isEqualTo(3);
        assertThat(orderedSections.get(1).getDownStation())
            .isEqualTo(orderedSections.get(2).getUpStation());
        assertThat(orderedSections.get(2).getDistance()).isEqualTo(originalDistance - distance);
    }

    @DisplayName("상행 종점을 추가한다.")
    @Test
    void add_first_section() {
        //given
        Section section = Section
            .of(LineTest.LINE_2, StationTest.STATION_1, StationTest.STATION_2, 10);

        //when
        SECTIONS.addSection(section);

        //then
        assertThat(SECTIONS.getSections().size()).isEqualTo(3);
        assertThat(SECTIONS.orderedSections().get(0)).isEqualTo(section);
    }


    @DisplayName("하행 종점을 추가한다.")
    @Test
    void add_last_section() {
        //given
        Section section = Section
            .of(LineTest.LINE_2, StationTest.STATION_3, StationTest.STATION_1, 10);

        //when
        SECTIONS.addSection(section);

        //then
        assertThat(SECTIONS.getSections().size()).isEqualTo(3);
        assertThat(SECTIONS.orderedSections().get(2)).isEqualTo(section);
    }


    @DisplayName("노선에 이미 존재하는 구간은 동일 노선에 추가할 수 없다.")
    @Test
    void add_section_with_equal_stations_is_invalid() {
        //given
        Section section = Section
            .of(LineTest.LINE_2, StationTest.STATION_2, StationTest.STATION_4, 2);

        //when
        assertThatThrownBy(() -> SECTIONS.addSection(section))
            .isInstanceOf(SectionAlreadyExistInTheLineException.class)
            .hasMessageContaining(SECTION_ALREADY_EXIST_IN_THE_LINE_EXCEPTION);

    }

    @DisplayName("이미 존재하는 구간의 거리보다 더 큰 거리를 가진 역은 사이에 추가할 수 없다.")
    @Test
    void add_inner_section_with_smaller_distance_is_invalid() {
        //given
        Section section = Section
            .of(LineTest.LINE_2, StationTest.STATION_4, StationTest.STATION_1, 12);

        //when
        assertThatThrownBy(() -> SECTIONS.addSection(section))
            .isInstanceOf(SectionDistanceExceededException.class)
            .hasMessageContaining(SECTION_DISTANCE_EXCEEDED_EXCEPTION);

    }

    @DisplayName("구간의 두개의 역 중 하나는 노선에 존재해야 한다.")
    @Test
    void add_section_with_stations_not_in_the_line_is_invalid() {
        //given
        Section section = Section
            .of(LineTest.LINE_2, StationTest.STATION_1, StationTest.STATION_5, 12);

        //when
        assertThatThrownBy(() -> SECTIONS.addSection(section))
            .isInstanceOf(StationsNotExistInTheLine.class)
            .hasMessageContaining(STATIONS_NOT_EXIST_IN_THE_LINE_EXCEPTION);

    }


}