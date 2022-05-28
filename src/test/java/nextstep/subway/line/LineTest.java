package nextstep.subway.line;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


@DisplayName("노선 도메인")
public class LineTest {

    @Test
    @DisplayName("노선의 이름을 필수 이다.")
    void nameIsNotEmpty() {
        assertAll(
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> new Line("","bg-red" ,new Station("인천역"), new Station("주안역"), Distance.of(2))),

                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> new Line(null, "bg-red",new Station("인천역"), new Station("주안역"), Distance.of(2)))
        );
    }

    @Test
    @DisplayName("색상은 필수 이다.")
    void colorIsNotEmpty() {
        assertAll(
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> new Line("1호선",null ,new Station("인천역"), new Station("주안역"), Distance.of(2))),

                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> new Line("1호선", "",new Station("인천역"), new Station("주안역"), Distance.of(2)))
        );
    }


    @Test
    @DisplayName("노선이 생성 된다.")
    void createLine() {
        Line line = new Line("이름","bg-red" ,new Station("인천역"), new Station("주안역"), Distance.of(3));
        assertThat(line.getName()).isEqualTo("이름");
    }
}
