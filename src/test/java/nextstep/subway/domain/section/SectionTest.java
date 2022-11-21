package nextstep.subway.domain.section;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.SectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Section 도메인")
public class SectionTest {

    @Nested
    @DisplayName("생성자는")
    class DescribeConstructor {

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
    class DescribeGetStations {

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
                    () -> assertThat(section.getStations()).containsExactly(upStation, downStation)
            );
        }
    }

    @Nested
    @DisplayName("isShortDistance 메소드")
    class DescribeIsShortDistance {

        private Line line = new Line("2호선", "green");
        private Station upStation = new Station("강남역");
        private Station downStation = new Station("잠실역");
        private Section section;
        private Section newSection;

        @BeforeEach
        void setUpSection() {
            section = new Section(line, upStation, downStation, 10);
            newSection = new Section(new Line("3호선", "yellow"), downStation, upStation, 50);
        }

        @DisplayName("입력 구간보다 거리값이 작거나 같으면 true를 반환")
        @Test
        void context_with_less_then_distance_returns_true() {
            assertThat(section.isShortDistance(newSection)).isTrue();
        }
    }

    @Nested
    @DisplayName("isSameUpStation 메소드")
    class DescribeIsSameUpStation {

        private Line line = new Line("2호선", "green");
        private Station upStation = new Station("강남역");
        private Station downStation = new Station("잠실역");
        private Section section;
        private Section newSection;

        @BeforeEach
        void setUpSection() {
            section = new Section(line, upStation, downStation, 10);
            newSection = new Section(new Line("3호선", "yellow"), upStation, new Station("마곡역"), 50);
        }

        @DisplayName("입력구간과 같은 상행역이면 true를 반환")
        @Test
        void context_with_same_upstation_returns_true() {
            assertThat(section.isSameUpStation(newSection)).isTrue();
        }
    }

    @Nested
    @DisplayName("isSameDownStation 메소드")
    class DescribeIsSameDownStation {

        private Line line = new Line("2호선", "green");
        private Station upStation = new Station("강남역");
        private Station downStation = new Station("잠실역");
        private Section section;
        private Section newSection;

        @BeforeEach
        void setUpSection() {
            section = new Section(line, upStation, downStation, 10);
            newSection = new Section(new Line("3호선", "yellow"), new Station("마곡역"), downStation, 50);
        }

        @DisplayName("입력구간과 같은 하행역이면 true를 반환")
        @Test
        void context_with_same_upstation_returns_true() {
            assertThat(section.isSameDownStation(newSection)).isTrue();
        }
    }

    @Nested
    @DisplayName("isComponentAllOfStations 메소드")
    class DescribeIsComponentAllOfStations {

        private Line line = new Line("2호선", "green");
        private Station upStation = new Station("강남역");
        private Station downStation = new Station("잠실역");
        private Section section;
        private List<Station> stations;

        @BeforeEach
        void setUpSection() {
            section = new Section(line, upStation, downStation, 10);
            stations = new ArrayList<>();
            stations.add(upStation);
            stations.add(downStation);
            stations.add(new Station("마곡역"));
        }

        @DisplayName("입력으로 주어진 역들이 구간에 있는 역을 모두 포함한다면 true를 반환")
        @Test
        void context_with_same_upstation_returns_true() {
            assertThat(section.isComponentAllOfStations(stations)).isTrue();
        }
    }

    @Nested
    @DisplayName("isComponentAnyOfStations 메소드")
    class DescribeIsComponentAnyOfStations {

        private Line line = new Line("2호선", "green");
        private Station upStation = new Station("강남역");
        private Station downStation = new Station("잠실역");
        private Section section;
        private List<Station> stations;

        @BeforeEach
        void setUpSection() {
            section = new Section(line, upStation, downStation, 10);
            stations = new ArrayList<>();
            stations.add(upStation);
            stations.add(new Station("마곡역"));
        }

        @DisplayName("입력으로 주어진 역들이 구간에 있는 역을 하나라도 포함한다면 true를 반환 ")
        @Test
        void context_with_same_upstation_returns_true() {
            assertThat(section.isComponentAnyOfStations(stations)).isTrue();
        }
    }

    @Nested
    @DisplayName("modifyUpStation 메소드")
    class DescribeModifyUpStation {

        private Line line = new Line("2호선", "green");
        private Station gangnameStation = new Station("강남역");
        private Station jamsilStation = new Station("잠실역");
        private Station bangbaeStation = new Station("방배역");
        private Station samsungStation = new Station("삼성역");
        private Section section;
        private Section newSection;

        @BeforeEach
        void setUpSection() {
            section = new Section(line, gangnameStation, jamsilStation, 10);
            newSection = new Section(line, bangbaeStation, samsungStation, 3);
        }

        @DisplayName("상행역을 주어진 구간의 하행역으로 바꾼다")
        @Test
        void context_with_section_modify_upstation() {
            section.modifyUpStation(newSection);
            assertAll(
                    () -> assertThat(section.getUpStation()).isEqualTo(samsungStation),
                    () -> assertThat(section.getDistance()).isEqualTo(7)
            );
        }
    }

    @Nested
    @DisplayName("modifyDownStation 메소드")
    class DescribeModifyDownStation {

        private Line line = new Line("2호선", "green");
        private Station gangnameStation = new Station("강남역");
        private Station jamsilStation = new Station("잠실역");
        private Station bangbaeStation = new Station("방배역");
        private Station samsungStation = new Station("삼성역");
        private Section section;
        private Section newSection;

        @BeforeEach
        void setUpSection() {
            section = new Section(line, gangnameStation, jamsilStation, 10);
            newSection = new Section(line, bangbaeStation, samsungStation, 3);
        }

        @DisplayName("하행역을 주어진 구간의 상행역으로 바꾼다")
        @Test
        void context_with_section_modify_upstation() {
            section.modifyDownStation(newSection);
            assertAll(
                    () -> assertThat(section.getDownStation()).isEqualTo(bangbaeStation),
                    () -> assertThat(section.getDistance()).isEqualTo(7)
            );
        }
    }
}

