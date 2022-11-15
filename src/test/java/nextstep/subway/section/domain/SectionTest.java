package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionTest {
    private Station upStation;
    private Station downStation;
    private int distance;

    @BeforeEach
    void setUp() {
        this.upStation = Station.from("신사역");
        this.downStation = Station.from("광교역");
        this.distance = 10;
    }

    @DisplayName("상행역이 다른 지하철 구간은 동등하지 않다.")
    @Test
    void equalsFail() {
        Section actual = Section.of(upStation, downStation, distance);
        Section expected = Section.of(Station.from("강남역"), downStation, distance);

        assertThat(actual).isNotEqualTo(expected);
    }

    @DisplayName("하행역이 다른 지하철 구간은 동등하지 않다.")
    @Test
    void equalsFail2() {
        Section actual = Section.of(upStation, downStation, distance);
        Section expected = Section.of(upStation, Station.from("강남역"), distance);

        assertThat(actual).isNotEqualTo(expected);
    }

    @DisplayName("속해있는 지하철 노선이 다른 지하철 구간은 동등하지 않다.")
    @Test
    void equalsFail3() {
        Section actual = Section.of(upStation, downStation, distance);
        actual.addTo(Line.of("신분당선", "red", actual));

        Section expected = Section.of(upStation, downStation, distance);
        expected.addTo(Line.of("분당선", "yellow", expected));

        assertThat(actual).isNotEqualTo(expected);
    }

    @DisplayName("상행역, 하행역, 속한 지하철 노선이 같은 지하철 구간은 동등하다.")
    @Test
    void equalsSuccess() {
        Section actual = Section.of(upStation, downStation, distance);
        actual.addTo(Line.of("신분당선", "red", actual));

        Section expected = Section.of(upStation, downStation, distance);
        expected.addTo(Line.of("신분당선", "red", expected));

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("지하철 구간 생성 시 상행역이 null 이면 예외가 발생한다.")
    @Test
    void upStationIsNull() {
        Assertions.assertThatThrownBy(() -> Section.of(null, downStation, distance))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 구간 생성 시 하행역이 null 이면 예외가 발생한다.")
    @Test
    void downStationIsNull() {
        Assertions.assertThatThrownBy(() -> Section.of(upStation, null, distance))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 구간 생성 시 상행역과 하행역이 같으면 예외가 발생한다.")
    @Test
    void upStationEqualsDownStation() {
        Assertions.assertThatThrownBy(() -> Section.of(upStation, upStation, distance))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 구간 길이 0 이하이면 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void invalidDistance(int input) {
        Assertions.assertThatThrownBy(() -> Section.of(upStation, downStation, input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 구간 생성")
    @Test
    void createSection() {
        Section section = Section.of(upStation, downStation, distance);
        assertThat(section).isNotNull();
    }

    @DisplayName("지하철 구간의 상행역과 거리가 수정되는 것을 확인한다.")
    @Test
    void update() {
        Section section = Section.of(upStation, downStation, distance);
        section.update(Station.from("강남역"), Distance.from(5));

        assertAll(
                () -> assertThat(section.getUpStation()).isEqualTo(Station.from("강남역")),
                () -> assertThat(section.getDistance()).isEqualTo(Distance.from(5))
        );
    }
}
