package nextstep.subway.line.domain;

import nextstep.subway.common.exception.DataRemoveException;
import nextstep.subway.common.message.ExceptionMessage;
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

    @DisplayName("지하철 이름, 색상 동일한 지하철 노선은 동등하다.")
    @Test
    void equals1() {
        Line actual = Line.of("신분당선", "bg-red-500", Section.of(upStation, downStation, distance));
        Line expected = Line.of("신분당선", "bg-red-500", Section.of(upStation, downStation, distance));

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("지하철 이름이 다른 지하철 노선은 동등하지 않다.")
    @Test
    void equals2() {
        Line actual = Line.of("신분당선", "bg-red-500", Section.of(upStation, downStation, distance));
        Line expected = Line.of("분당선", "bg-red-500", Section.of(upStation, downStation, distance));

        assertThat(actual).isNotEqualTo(expected);
    }

    @DisplayName("지하철 색상이 다른 지하철 노선은 동등하지 않다.")
    @Test
    void equals3() {
        Line actual = Line.of("신분당선", "bg-red-500", Section.of(upStation, downStation, distance));
        Line expected = Line.of("신분당선", "bg-yellow-500", Section.of(upStation, downStation, distance));

        assertThat(actual).isNotEqualTo(expected);
    }

    @DisplayName("지하철 노선에서 상행종점역을 제거하면 다음역이 상행종점역이 된다.")
    @Test
    void removeSection() {
        Line line = Line.of("신분당선", "red", Section.of(upStation, downStation, distance));
        Section 신사역_강남역_구간 = Section.of(upStation, Station.from("강남역"), 5);
        line.addSection(신사역_강남역_구간);

        line.removeSection(upStation);

        assertThat(line.getStationsInOrder())
                .containsExactly(
                        Station.from("강남역"),
                        Station.from("광교역")
                );
    }

    @DisplayName("지하철 노선에서 하행종점역을 제거하면 이전역이 하행종점역이 된다.")
    @Test
    void removeSection2() {
        Line line = Line.of("신분당선", "red", Section.of(upStation, downStation, distance));
        Section 신사역_강남역_구간 = Section.of(upStation, Station.from("강남역"), 5);
        line.addSection(신사역_강남역_구간);

        line.removeSection(downStation);

        assertThat(line.getStationsInOrder())
                .containsExactly(
                        Station.from("신사역"),
                        Station.from("강남역")
                );
    }

    @DisplayName("지하철 노선에서 중간역을 제거하면 구간이 재배치된다.")
    @Test
    void removeSection3() {
        Line line = Line.of("신분당선", "red", Section.of(upStation, downStation, distance));
        Station 강남역 = Station.from("강남역");
        Section 신사역_강남역_구간 = Section.of(upStation, 강남역, 5);
        line.addSection(신사역_강남역_구간);

        line.removeSection(강남역);

        assertThat(line.getStationsInOrder())
                .containsExactly(
                        Station.from("신사역"),
                        Station.from("광교역")
                );
    }

    @DisplayName("지하철 노선에 자하철 구간이 하나인 경우 지하철역 제거 시 예외가 발생한다.")
    @Test
    void removeSectionException() {
        Line line = Line.of("신분당선", "red", Section.of(upStation, downStation, distance));

        Assertions.assertThatThrownBy(() -> line.removeSection(upStation))
                .isInstanceOf(DataRemoveException.class)
                .hasMessageStartingWith(ExceptionMessage.FAIL_TO_REMOVE_STATION_FROM_ONE_SECTION);
    }

    @DisplayName("지하철 노선에 존재하지않는 자하철 구간 제거시 예외가 발생한다.")
    @Test
    void removeSectionException2() {
        Line line = Line.of("신분당선", "red", Section.of(upStation, downStation, distance));
        Section 신사역_강남역_구간 = Section.of(upStation, Station.from("강남역"), 5);
        line.addSection(신사역_강남역_구간);

        Station 수원역 = Station.from("수원역");
        Assertions.assertThatThrownBy(() -> line.removeSection(수원역))
                .isInstanceOf(DataRemoveException.class)
                .hasMessageStartingWith(ExceptionMessage.NOT_FOUND_STATION);
    }
}
