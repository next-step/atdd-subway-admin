package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.subway.global.exception.BadRequestException;
import nextstep.subway.global.exception.ExceptionType;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("노선에 대한 단위 테스트")
class LineTest {

    private Station upStation;
    private Station downStation;

    @DisplayName("노선 이름이 없으면 예외가 발생해야 한다")
    @Test
    void line_exception_test() {
        // given
        upStation = new Station("테스트1");
        downStation = new Station("테스트2");

        assertThatThrownBy(() -> {
            Line.of(null, "red", 5L, upStation, downStation);
        }).isInstanceOf(BadRequestException.class)
            .hasMessageContaining(ExceptionType.IS_EMPTY_LINE_NAME.getMessage());
    }

    @DisplayName("노선 색상이 없으면 예외가 발생해야 한다")
    @Test
    void line_exception_test2() {
        // given
        upStation = new Station("테스트1");
        downStation = new Station("테스트2");

        assertThatThrownBy(() -> {
            Line.of("2호선", null, 5L, upStation, downStation);
        }).isInstanceOf(BadRequestException.class)
            .hasMessageContaining(ExceptionType.IS_EMPTY_LINE_COLOR.getMessage());
    }

    @DisplayName("노선 정보를 변경하면 정상적으로 변경되어야 한다")
    @Test
    void line_update_test() {
        upStation = new Station("테스트1");
        downStation = new Station("테스트2");

        Line line = Line.of("2호선", "red", 5L, upStation, downStation);
        line.update("3호선", "green");

        assertAll(
            () -> assertThat(line.getName()).isEqualTo("3호선"),
            () -> assertThat(line.getColor()).isEqualTo("green")
        );
    }
}
