package nextstep.subway.line.domain;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    Line sut;
    private Station A_Station;
    private Station C_Station;

    @BeforeEach
    void setUp() {
        A_Station = new Station(1L, "A");
        C_Station = new Station(2L, "C");
        sut = new Line(1L, "신분당역", "빨강", Sections.of(new ArrayList<>()));
        Section section = new Section(A_Station, C_Station, 100);
        sut.addSection(section);
    }

    @Test
    void targetSection이_중앙에_위치할_경우() {
        assertThat(sut.getStations()).containsExactly(A_Station, C_Station);

        Station originStation = new Station(3L, "B");
        final Section targetSection = new Section(A_Station, originStation, -40);
        sut.addSection(targetSection);

        assertThat(sut.getSections().getValues().stream().map(Section::getDistance)).containsExactly(40,60);
        assertThat(sut.getStations()).containsExactly(A_Station, originStation, C_Station);
    }

    @Test
    void 새로운_역을_상행_종점_등록() {
        assertThat(sut.getStations()).containsExactly(A_Station, C_Station);

        Station B_Station = new Station(3L, "B");
        final Section targetSection = new Section(B_Station, A_Station, 40);
        sut.addSection(targetSection);

        assertThat(sut.getStations()).containsExactly(B_Station, A_Station, C_Station);
    }

    @Test
    void 새로운_역을_하행_종점_등록() {
        assertThat(sut.getStations()).containsExactly(A_Station, C_Station);

        Station B_Station = new Station(3L, "B");
        final Section targetSection = new Section(C_Station, B_Station, -40);
        sut.addSection(targetSection);

        assertThat(sut.getStations()).containsExactly(A_Station, C_Station, B_Station);
    }
}
