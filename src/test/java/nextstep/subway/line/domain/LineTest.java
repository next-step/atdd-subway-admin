package nextstep.subway.line.domain;

import nextstep.subway.exception.InvalidSectionException;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class LineTest {

    Line sut;
    private Station A_Station;
    private Station C_Station;
    private Station NOT_INCLUDE_STATION;

    @BeforeEach
    void setUp() {
        A_Station = new Station(1L, "A");
        C_Station = new Station(2L, "C");
        NOT_INCLUDE_STATION = new Station(100L, "NOT_INCLUDE");
        LinkedList<Section> sections = new LinkedList<>();
        Section section = Section.of(A_Station, C_Station, 100);
        sections.add(section);
        sut = new Line(1L, "신분당역", "빨강", Sections.of(sections));
    }

    @Test
    void targetSection이_중앙에_위치할_경우() {
        assertThat(sut.getStations()).containsExactly(A_Station, C_Station);

        Station originStation = new Station(3L, "B");


        final Section targetSection = Section.of(A_Station, originStation, 40);
        sut.addSection(targetSection);

        assertThat(sut.getStations()).containsExactly(A_Station, originStation, C_Station);
    }

    @Test
    void targetSection이_중앙에_위치할_경우2() {
        assertThat(sut.getStations()).containsExactly(A_Station, C_Station);

        Station originStation = new Station(3L, "B");
        final Section targetSection = Section.of(originStation, C_Station, 40);
        sut.addSection(targetSection);

        assertThat(sut.getSections().stream()
                .map(Section::getDistance)).containsExactly(60, 40);
        assertThat(sut.getStations()).containsExactly(A_Station, originStation, C_Station);
    }

    @Test
    void 새로운_역을_상행_종점_등록() {
        assertThat(sut.getStations()).containsExactly(A_Station, C_Station);

        Station B_Station = new Station(3L, "B");
        final Section targetSection = Section.of(B_Station, A_Station, 40);
        sut.addSection(targetSection);

        assertThat(sut.getStations()).containsExactly(B_Station, A_Station, C_Station);
    }

    @Test
    void 새로운_역을_하행_종점_등록() {
        assertThat(sut.getStations()).containsExactly(A_Station, C_Station);

        Station B_Station = new Station(3L, "B");
        final Section targetSection = Section.of(C_Station, B_Station, -40);
        sut.addSection(targetSection);

        assertThat(sut.getStations()).containsExactly(A_Station, C_Station, B_Station);
    }

    @Test
    void 종점이_제거될_경우_다음으로_오던_역이_종점이_됨() {
        assertThat(sut.getStations()).containsExactly(A_Station, C_Station);
        Station B_Station = new Station(3L, "B");
        sut.addSection(Section.of(B_Station, C_Station, 40));

        assertThat(sut.getSections().stream().map(Section::getDistance)).containsExactly(60, 40);
        assertThat(sut.getStations()).containsExactly(A_Station, B_Station, C_Station);

        sut.removeStation(B_Station);
        assertThat(sut.getSections().stream().map(Section::getDistance)).containsExactly(100);
        assertThat(sut.getStations()).containsExactly(A_Station, C_Station);
    }

    @Test
    void 제거할수_없는_역을_구간에서_제거할_경우_예외_발생() {
        assertThat(sut.getStations()).containsExactly(A_Station, C_Station);
        Station B_Station = new Station(3L, "B");
        sut.addSection(Section.of(B_Station, C_Station, 40));

        assertThat(sut.getSections().stream().map(Section::getDistance)).containsExactly(60, 40);
        assertThat(sut.getStations()).containsExactly(A_Station, B_Station, C_Station);

        assertThatThrownBy(() -> sut.removeStation(NOT_INCLUDE_STATION))
                .isInstanceOf(InvalidSectionException.class)
                .hasMessageContaining("adjacentSections");

    }

    @Test
    void 종점을_제거할_경우_예외_발생() {
        assertThat(sut.getStations()).containsExactly(A_Station, C_Station);
        Station B_Station = new Station(3L, "B");
        sut.addSection(Section.of(B_Station, C_Station, 40));

        assertThat(sut.getSections().stream().map(Section::getDistance)).containsExactly(60, 40);
        assertThat(sut.getStations()).containsExactly(A_Station, B_Station, C_Station);

        assertThatThrownBy(() -> sut.removeStation(NOT_INCLUDE_STATION))
                .isInstanceOf(InvalidSectionException.class)
                .hasMessageContaining("adjacentSections");
    }
}
