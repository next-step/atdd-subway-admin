package nextstep.subway.unit.line;

import nextstep.subway.application.exception.exception.NotValidDataException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static nextstep.subway.application.exception.type.ValidExceptionType.NOT_VALID_COLOR;
import static nextstep.subway.application.exception.type.ValidExceptionType.NOT_VALID_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LineUnitTest {

    @Test
    @DisplayName("이름이 존재하지 않으면 라인을 생성할 수 없다")
    void notNameException() {
        Station upStation = new Station("선릉역");
        Station downStation = new Station("역삼역");

        assertThatThrownBy(() -> {
            Line.of(null, "red", upStation, downStation, 5);
        }).isInstanceOf(NotValidDataException.class)
                .hasMessageContaining(NOT_VALID_NAME.getMessage());
    }

    @Test
    @DisplayName("색깔이 존재하지 않으면 라인을 생성할 수 없다")
    void notColorException() {
        Station upStation = new Station("선릉역");
        Station downStation = new Station("역삼역");

        assertThatThrownBy(() -> {
            Line.of("1호선", null, upStation, downStation, 5);
        }).isInstanceOf(NotValidDataException.class)
                .hasMessageContaining(NOT_VALID_COLOR.getMessage());
    }

    @Test
    @DisplayName("성공적으로 라인을 생성한다")
    void createLineSuccess() {
        String name = "1호선";
        String color = "red";
        String upStationName = "선릉역";
        String downStationName = "역삼역";
        Station upStation = new Station(upStationName);
        Station downStation = new Station(downStationName);

        Line line = Line.of(name, color, upStation, downStation, 5);
        List<Station> lineStations = line.getLineStations();

        assertAll(
                () -> assertEquals(line.getName(), name),
                () -> assertEquals(line.getColor(), color)
        );
        assertThat(lineStations.get(0).getName()).isEqualTo(upStationName);
        assertThat(lineStations.get(1).getName()).isEqualTo(downStationName);
    }

    @Test
    @DisplayName("성공적으로 라인을 이름 과 색깔을 업데이트한다")
    void updateLineSuccess() {
        Station upStation = new Station("선릉역");
        Station downStation = new Station("역삼역");
        Line line = Line.of("1호산", "red", upStation, downStation, 5);

        line.updateNameAndColor("2호선", "blue");

        assertThat(line.getColor()).isEqualTo("blue");
        assertThat(line.getName()).isEqualTo("2호선");
    }
}