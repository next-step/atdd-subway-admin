package nextstep.subway.domain;


import static nextstep.subway.domain.Station.createStation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


@DisplayName("노선 도메인")
public class LineTest {
    Line changeLine;

    @BeforeEach
    void setUp() {
        changeLine = Line.builder().name("1호선").color("bg-red-500")
                .upStation(createStation("인천역")).downStation(createStation("주안역"))
                .distance(Distance.of(2)).build();
    }

    @Test
    @DisplayName("노선의 이름을 필수 이다.")
    void nameIsNotEmpty() {

        assertAll(
            () -> assertThatIllegalArgumentException().isThrownBy(() -> Line.builder().name("")
                        .color("bg-red")
                        .upStation(createStation("인천역"))
                        .downStation(createStation("주안역"))
                        .distance(Distance.of(2))
                        .build()),
            () -> assertThatIllegalArgumentException().isThrownBy(() -> Line.builder().name(null)
                        .color("bg-red")
                        .upStation(createStation("인천역"))
                        .downStation(createStation("주안역"))
                        .distance(Distance.of(2))
                        .build())
        );
    }

    @Test
    @DisplayName("색상은 필수 이다.")
    void colorIsNotEmpty() {
        assertAll(
                () -> assertThatIllegalArgumentException().isThrownBy(() -> Line.builder()
                        .name("1호선").color("")
                        .upStation(createStation("인천역")).downStation(createStation("주안역"))
                        .distance(Distance.of(2))
                        .build()),
                () -> assertThatIllegalArgumentException().isThrownBy(() -> Line.builder()
                        .name("1호선").color(null)
                        .upStation(createStation("인천역")).downStation(createStation("주안역"))
                        .distance(Distance.of(2))
                        .build()));
    }

    @Test
    @DisplayName("노선이 생성 된다.")
    void createLine() {
        Line line = Line.builder().name("이름").color("bg-red-500")
                .upStation(createStation("인천역")).downStation(createStation("주안역"))
                .distance(Distance.of(2)).build();

        assertThat(line.getName()).isEqualTo("이름");
    }

    @Test
    @DisplayName("노선의 이름이 변경된다")
    void changeName() {
        changeLine.changeName("이름2");

        assertThat(changeLine.getName()).isEqualTo("이름2");
    }

    @Test
    @DisplayName("변경할 이름이 공백이면 안된다.")
    void changeNameIsNotEmpty() {
        assertAll(
                () -> assertThatIllegalArgumentException().isThrownBy(() -> changeLine.changeName("")),
                () -> assertThatIllegalArgumentException().isThrownBy(() -> changeLine.changeName(null))
        );
    }

    @Test
    @DisplayName("노선의 색상이 변경된다")
    void changeColor() {
        changeLine.changeColor("색상");
        assertThat(changeLine.getColor()).isEqualTo("색상");
    }


    @Test
    @DisplayName("변경할 색상이 공백이면 안된다.")
    void changeColorIsNotEmpty() {
        assertAll(
                () -> assertThatIllegalArgumentException().isThrownBy(() -> changeLine.changeColor("")),
                () -> assertThatIllegalArgumentException().isThrownBy(() -> changeLine.changeColor(null))
        );
    }
}
