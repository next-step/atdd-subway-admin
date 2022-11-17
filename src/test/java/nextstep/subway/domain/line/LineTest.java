package nextstep.subway.domain.line;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.LineException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Line 클래스")
public class LineTest {

    @Nested
    @DisplayName("생성자는")
    class Describe_constructor {

        @DisplayName("이름이 null이면 예외를 반환")
        @Test
        void context_with_null_name_returns_exception() {
            assertThatThrownBy(() -> new Line(null, "green"))
                    .isInstanceOf(LineException.class)
                    .hasMessage("노선이름을 입력해야합니다");
        }

        @DisplayName("이름이 빈값이면 예외를 반환")
        @Test
        void context_with_empty_name_returns_exception() {
            assertThatThrownBy(() -> new Line("", "green"))
                    .isInstanceOf(LineException.class)
                    .hasMessage("노선이름을 입력해야합니다");
        }

        @DisplayName("color가 null이면 예외를 반환")
        @Test
        void context_with_null_color_returns_exception() {
            assertThatThrownBy(() -> new Line("2호선", null))
                    .isInstanceOf(LineException.class)
                    .hasMessage("노선컬러를 입력해야합니다");
        }

        @DisplayName("color가 빈값이면 예외를 반환")
        @Test
        void context_with_empty_color_returns_exception() {
            assertThatThrownBy(() -> new Line("2호선", ""))
                    .isInstanceOf(LineException.class)
                    .hasMessage("노선컬러를 입력해야합니다");
        }

    }

    @Nested
    @DisplayName("addSection 메소드는")
    class Describe_addSection {

        private Line line = new Line("2호선", "green");
        private Station upStation = new Station("강남역");
        private Station downStation = new Station("잠실역");

        @BeforeEach
        void setUpSection() {
            line.addSection(new Section(line, upStation, downStation, 10));
        }

        @DisplayName("section이 null이면 예외를 반환")
        @Test
        void context_with_null_returns_exception() {
            assertThatThrownBy(() -> line.addSection(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("구간정보가 존재하지 않습니다");
        }

        @DisplayName("section을 추가하면 getStation메소드에서 추가한역 조회")
        @Test
        void context_with_section_returns_station() {
            assertThat(line.getStations()).containsExactly(upStation, downStation);
        }

    }

    @Nested
    @DisplayName("getStations 메소드는")
    class Describe_getStations {

        private Line line = new Line("2호선", "green");
        private Station gangnamStation = new Station("강남역");
        private Station jamsilStation = new Station("잠실역");
        private Station bangbaeStation = new Station("방배역");

        @BeforeEach
        void setUpSection() {
            line.addSection(new Section(line, gangnamStation, jamsilStation, 10));
            line.addSection(new Section(line, jamsilStation, bangbaeStation, 7));
        }

        @DisplayName("Line에 존재하는 역을 중복없이 조회")
        @Test
        void context_with_null_returns_exception() {
            assertAll(
                    () -> assertThat(line.getStations()).hasSize(3),
                    () -> assertThat(line.getStations()).containsExactly(gangnamStation, jamsilStation, bangbaeStation)
            );
        }
    }
}
