package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineTest {
    private Station upStation;
    private Station downStation;
    private Line line;

    @BeforeEach
    void setUp() {
        upStation = new Station("강남역");
        downStation = new Station("판교역");
        line = new Line("신분당선", "red");
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        line.addSection(new Section(upStation, downStation, new Distance(20)));

        assertAll(() -> {
            assertThat(line.getName()).isEqualTo("신분당선");
            assertThat(line.getColor()).isEqualTo("red");
            assertThat(line.getSections().values()).hasSize(1);
        });
    }

    @DisplayName("노선 정보를 수정한다.")
    @Test
    void updateLine() {
        line.update("서울역", "green");

        assertAll(() -> {
            assertThat(line.getName()).isEqualTo("서울역");
            assertThat(line.getColor()).isEqualTo("green");
        });
    }
}
