package nextstep.subway.line.domain;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineTest {

    private Station upStation;
    private Station downStation;
    private int distance;

    @BeforeEach
    void setUp() {
        upStation = Station.from("신사역");
        downStation = Station.from("광교역");
        distance = 10;
    }

    @DisplayName("지하철 노선 생성 시 필수값 테스트 (이름)")
    @Test
    void createLine1() {
        Section section = Section.of(upStation, downStation, distance);
        Assertions.assertThatThrownBy(() -> Line.of(null, "bg-red-600", section))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 노선 생성 시 필수값 테스트 (색상)")
    @Test
    void createLine2() {
        Section section = Section.of(upStation, downStation, distance);
        Assertions.assertThatThrownBy(() -> Line.of("신분당선", null, section))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 노선 생성 (정상케이스)")
    @Test
    void createLine3() {
        Line line = Line.of("신분당선", "bg-red-500", Section.of(upStation, downStation, distance));
        assertThat(line).isNotNull();
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        Line line = Line.of("신분당선", "bg-red-500", Section.of(upStation, downStation, distance));

        line.update("분당선", "yellow");

        assertAll(
                () -> assertThat(line.getName()).isEqualTo("분당선"),
                () -> assertThat(line.getColor()).isEqualTo("yellow")
        );
    }

    @DisplayName("지하철 노선에 지하철 구간 추가")
    @Test
    void addTo() {
        Line line = Line.of("신분당선", "red", Section.of(upStation, downStation, distance));

        Section 신사역_강남역_구간 = Section.of(upStation, Station.from("강남역"), 5);
        line.addSection(신사역_강남역_구간);

        assertThat(line.getStationsInOrder())
                .containsExactly(
                        Station.from("신사역"),
                        Station.from("강남역"),
                        Station.from("광교역")
                );
    }

    @DisplayName("지하철 노선 동등성 테스트")
    @Test
    void equals1() {
        Line actual = Line.of("신분당선", "bg-red-500", Section.of(upStation, downStation, distance));
        Line expected = Line.of("신분당선", "bg-red-500", Section.of(upStation, downStation, distance));

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("지하철 노선 동등성 테스트2")
    @Test
    void equals2() {
        Line actual = Line.of("신분당선", "bg-red-500", Section.of(upStation, downStation, distance));
        Line expected = Line.of("분당선", "bg-red-500", Section.of(upStation, downStation, distance));

        assertThat(actual).isNotEqualTo(expected);
    }
}
