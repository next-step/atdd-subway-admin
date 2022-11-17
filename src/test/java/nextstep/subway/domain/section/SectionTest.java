package nextstep.subway.domain.section;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.SectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Section 클래스")
public class SectionTest {

    @Nested
    @DisplayName("생성자는")
    class Describe_constructor {

        private Line line;
        private Station upStation;
        private Station downStation;

        @BeforeEach
        void setUpSection() {
            line = new Line("2호선", "green");
            upStation = new Station("강남역");
            downStation = new Station("잠실역");
        }

        @DisplayName("거리값이 0보다 작거나 같으면 예외가 발생")
        @Test
        void context_with_null_name_returns_exception() {
            assertThatThrownBy(() -> new Section(line, upStation, downStation, 0))
                    .isInstanceOf(SectionException.class)
                    .hasMessage("거리값은 양수만 허용합니다");
        }
    }

    @Nested
    @DisplayName("getStations 메소드")
    class Describe_getStations {
        private Line line = new Line("2호선", "green");
        private Station upStation = new Station("강남역");
        private Station downStation = new Station("잠실역");
        private Section section;

        @BeforeEach
        void setUpSection() {
            section = new Section(line, upStation, downStation, 10);
        }

        @DisplayName("상행역 하행역을 리스트로 반환")
        @Test
        void returns_stations() {
            assertAll(
                   () -> assertThat(section.getStations()).hasSize(2),
                   () -> assertThat(section.getStations()).containsExactly(upStation,downStation)
            );
        }
    }


}
