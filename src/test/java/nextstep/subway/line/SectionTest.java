package nextstep.subway.line;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class SectionTest {
    private static final int DEFAULT_DISTANCE = 10;
    Line line;
    Station stationA;
    Station stationB;
    Section section;

    @BeforeEach
    void setUp() {
        line = new Line("2호선", "green");
        stationA = new Station("강남역");
        stationB = new Station("교대역");
        section = new Section(line, stationA, stationB, DEFAULT_DISTANCE);
    }

    @DisplayName("구간의 상행선 하행선 중 특정 역이 포함하는 지 확인")
    @Test
    void contains() {
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(section.contains(stationA)).isTrue();
            softAssertions.assertThat(section.contains(stationB)).isTrue();
            softAssertions.assertThat(section.contains(new Station("서초역"))).isFalse();
        });
    }

    @DisplayName("구간과 구간이 안쪽으로 겹치는 지 확인")
    @Test
    void matchInside() {
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(section.matchInside(new Section(line, stationA, new Station("서초역"), 3))).isTrue();
            softAssertions.assertThat(section.matchInside(new Section(line, new Station("서초역"), stationB, 3))).isTrue();
            softAssertions.assertThat(section.matchInside(new Section(line, stationB, new Station("서초역"), 3))).isFalse();
            softAssertions.assertThat(section.matchInside(new Section(line, new Station("서초역"), stationA, 3))).isFalse();
        });
    }

    @DisplayName("구간과 구간이 바깥으로 겹치는 지 확인")
    @Test
    void matchOutside() {
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(section.matchOutside(new Section(line, stationB, new Station("서초역"), 3))).isTrue();
            softAssertions.assertThat(section.matchOutside(new Section(line, new Station("서초역"), stationA, 3))).isTrue();
            softAssertions.assertThat(section.matchOutside(new Section(line, stationA, new Station("서초역"), 3))).isFalse();
            softAssertions.assertThat(section.matchOutside(new Section(line, new Station("서초역"), stationB, 3))).isFalse();
        });
    }

    @DisplayName("구간의 상행선끼리 겹칠 때 상행선이 수정되는 지 확인")
    @Test
    void modifyUpStation() {
        Station stationC = new Station("서초역");
        boolean result = section.modifyBy(new Section(line, stationA, stationC, 3));
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(result).isTrue();
            softAssertions.assertThat(section.getUpStation()).isSameAs(stationC);
            softAssertions.assertThat(section.getDownStation()).isSameAs(stationB);
            softAssertions.assertThat(section.getDistance()).isEqualTo(DEFAULT_DISTANCE - 3);
        });
    }

    @DisplayName("구간의 하행선끼리 겹칠 때 하행선이 수정되는 지 확인")
    @Test
    void modifyDownStation() {
        Station stationC = new Station("서초역");
        boolean result = section.modifyBy(new Section(line, stationC, stationB, 3));
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(result).isTrue();
            softAssertions.assertThat(section.getUpStation()).isSameAs(stationA);
            softAssertions.assertThat(section.getDownStation()).isSameAs(stationC);
            softAssertions.assertThat(section.getDistance()).isEqualTo(DEFAULT_DISTANCE - 3);
        });
    }

    @DisplayName("추가하려는 구간이 기존 구간보다 같거나 길면 안된다")
    @Test
    void distanceTooLong() {
        assertThatThrownBy(() -> section.modifyBy(new Section(line, new Station("서초역"), stationB, DEFAULT_DISTANCE)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("두 구간을 합친다")
    @Test
    void merge() {
        Station stationC = new Station("서초역");
        Section newSection = new Section(line, stationB, stationC, 3);
        section.merge(newSection);
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(section.getUpStation()).isEqualTo(stationA);
            softAssertions.assertThat(section.getDownStation()).isEqualTo(stationC);
            softAssertions.assertThat(section.getDistance()).isEqualTo(DEFAULT_DISTANCE + 3);
        });
    }
}
