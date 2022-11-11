package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    private Station upStation;
    private Station downStation;

    @BeforeEach
    void setUp() {
        upStation = Station.from("신사역");
        downStation = Station.from("광교역");
    }

    @DisplayName("지하철 노선 생성 시 필수값 테스트 (이름)")
    @Test
    void createLine1() {
        Assertions.assertThatThrownBy(() -> Line.of(null, "bg-red-600", upStation, downStation))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 노선 생성 시 필수값 테스트 (색상)")
    @Test
    void createLine2() {
        Assertions.assertThatThrownBy(() -> Line.of("신분당선", null, upStation, downStation))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 생성 시 상행종점역이 null이면 예외를 발생시킨다.")
    @Test
    void upStationIsNull() {
        Assertions.assertThatThrownBy(() -> Line.of("신분당선", "bg-red-500", null, downStation))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 생성 시 하행종점역이 null이면 예외를 발생시킨다.")
    @Test
    void downStationIsNull() {
        Assertions.assertThatThrownBy(() -> Line.of("신분당선", "bg-red-500", upStation, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 생성 시 상행종점역과 하행종점역이 같으면 예외를 발생시킨다.")
    @Test
    void upStationEqualsDownStation() {
        Assertions.assertThatThrownBy(() -> Line.of("신분당선", "bg-red-500", upStation, upStation))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 노선 생성 (정상케이스)")
    @Test
    void createLine3() {
        Line line = Line.of("신분당선", "bg-red-500", upStation, downStation);
        Assertions.assertThat(line).isNotNull();
    }

    @DisplayName("지하철 노선 동등성 테스트")
    @Test
    void equals1() {
        Line actual = Line.of("신분당선", "bg-red-500", upStation, downStation);
        Line expected = Line.of("신분당선", "bg-red-500", upStation, downStation);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("지하철 노선 동등성 테스트2")
    @Test
    void equals2() {
        Line actual = Line.of("신분당선", "bg-red-500", upStation, downStation);
        Line expected = Line.of("분당선", "bg-red-500", upStation, downStation);

        Assertions.assertThat(actual).isNotEqualTo(expected);
    }
}
