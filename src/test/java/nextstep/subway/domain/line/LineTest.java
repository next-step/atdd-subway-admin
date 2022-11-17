package nextstep.subway.domain.line;

import nextstep.subway.domain.Line;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Line 클래스")
public class LineTest {

    @Nested
    @DisplayName("addSection 메소드는")
    class Describe_addSection {
        private Line line = new Line("1호선","blue");

        @DisplayName("null값이 주어지면 예외를 반환")
        @Test
        void context_with_null_returns_exception() {
            assertThatThrownBy(() -> line.addSection(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("구간정보가 존재하지 않습니다");
        }

        @DisplayName("section을 추가하면 getStations메소드에서 지하철역이 포함")
        @Test
        void context_with_section_returns_station() {

        }

    }

    @Nested
    @DisplayName("getStations 메소드는")
    class Describe_getStations {

    }

}
