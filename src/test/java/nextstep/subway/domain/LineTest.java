package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {
    private Line line;

    @BeforeEach
    void setUp() {
        line = Line.of("신분당선", "bg-red-600",
            Section.of(Station.from("논현역"), Station.from("강남역"), Distance.from(10)));
    }

    @Test
    @DisplayName("Line 생성")
    void createLine() {
        Line actual = Line.of("신분당선", "bg-red-600");
        assertAll(
            () -> assertThat(actual.getName()).isEqualTo("신분당선"),
            () -> assertThat(actual.getColor()).isEqualTo("bg-red-600")
        );
    }

    @Test
    @DisplayName("Line 생성시 이름, 색상 필수값")
    void createLineNotNullNameAndColor() {
        assertAll(
            () -> assertThrows(IllegalArgumentException.class, () -> Line.of(null, "bg-red-600")),
            () -> assertThrows(IllegalArgumentException.class, () -> Line.of("신분당선", null))
        );
    }

    @Test
    @DisplayName("노선 수정시 이름과 색상이 변경된다.")
    void updateLine() {
        Line actual = Line.of("신분당선", "bg-red-600");

        actual.updateLine("경춘선", "bg-emerald-600");

        assertAll(
            () -> assertThat(actual.getName()).isEqualTo("경춘선"),
            () -> assertThat(actual.getColor()).isEqualTo("bg-emerald-600")
        );
    }

    @Test
    @DisplayName("구간 내 상행 종점 역 제거")
    void removeSectionByUpStation() {
        // given
        Station upStation = Station.from("신사역");
        Station downStation = Station.from("논현역");
        Distance distance = Distance.from(5);
        line.addSection(Section.of(upStation, downStation, distance));

        // when
        line.removeSection(upStation);

        // then
        assertThat(line.getStations()).hasSize(2);
        assertThat(line.getStations()).doesNotContain(upStation);
        assertThat(line.totalDistance()).isEqualTo(10);
    }

    @Test
    @DisplayName("구간 내 하행 종점 역 제거")
    void removeSectionByDownStation() {
        // given
        Station upStation = Station.from("강남역");
        Station downStation = Station.from("판교역");
        Distance distance = Distance.from(10);
        line.addSection(Section.of(upStation, downStation, distance));

        // when
        line.removeSection(downStation);

        // then
        assertThat(line.getStations()).hasSize(2);
        assertThat(line.getStations()).doesNotContain(downStation);
        assertThat(line.totalDistance()).isEqualTo(10);
    }
}
