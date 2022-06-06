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
    Station 상행선;
    Station 하행선;
    Section 구간;

    @BeforeEach
    void setUp() {
        line = new Line("2호선", "green");
        상행선 = new Station("상행선");
        하행선 = new Station("하행선");
        구간 = new Section(line, 상행선, 하행선, DEFAULT_DISTANCE);
    }

    @DisplayName("구간의 상행선 하행선 중 특정 역이 포함하는 지 확인")
    @Test
    void contains() {
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(구간.contains(상행선)).isTrue();
            softAssertions.assertThat(구간.contains(하행선)).isTrue();
            softAssertions.assertThat(구간.contains(new Station("신규역"))).isFalse();
        });
    }

    @DisplayName("구간과 구간이 안쪽으로 겹치는 지 확인")
    @Test
    void matchInside() {
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(구간.matchInside(new Section(line, 상행선, new Station("신규역"), 3))).isTrue();
            softAssertions.assertThat(구간.matchInside(new Section(line, new Station("신규역"), 하행선, 3))).isTrue();
            softAssertions.assertThat(구간.matchInside(new Section(line, 하행선, new Station("신규역"), 3))).isFalse();
            softAssertions.assertThat(구간.matchInside(new Section(line, new Station("신규역"), 상행선, 3))).isFalse();
        });
    }

    @DisplayName("구간과 구간이 바깥으로 겹치는 지 확인")
    @Test
    void matchOutside() {
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(구간.matchOutside(new Section(line, 하행선, new Station("신규역"), 3))).isTrue();
            softAssertions.assertThat(구간.matchOutside(new Section(line, new Station("신규역"), 상행선, 3))).isTrue();
            softAssertions.assertThat(구간.matchOutside(new Section(line, 상행선, new Station("신규역"), 3))).isFalse();
            softAssertions.assertThat(구간.matchOutside(new Section(line, new Station("신규역"), 하행선, 3))).isFalse();
        });
    }

    @DisplayName("구간의 상행선끼리 겹칠 때 상행선이 수정되는 지 확인")
    @Test
    void modifyUpStation() {
        Station 중간역 = new Station("중간역");
        boolean result = 구간.modifyBy(new Section(line, 상행선, 중간역, 3));
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(result).isTrue();
            softAssertions.assertThat(구간.getUpStation()).isSameAs(중간역);
            softAssertions.assertThat(구간.getDownStation()).isSameAs(하행선);
            softAssertions.assertThat(구간.getDistance()).isEqualTo(DEFAULT_DISTANCE - 3);
        });
    }

    @DisplayName("구간의 하행선끼리 겹칠 때 하행선이 수정되는 지 확인")
    @Test
    void modifyDownStation() {
        Station 중간역 = new Station("중간역");
        boolean result = 구간.modifyBy(new Section(line, 중간역, 하행선, 3));
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(result).isTrue();
            softAssertions.assertThat(구간.getUpStation()).isSameAs(상행선);
            softAssertions.assertThat(구간.getDownStation()).isSameAs(중간역);
            softAssertions.assertThat(구간.getDistance()).isEqualTo(DEFAULT_DISTANCE - 3);
        });
    }

    @DisplayName("추가하려는 구간이 기존 구간보다 같거나 길면 안된다")
    @Test
    void distanceTooLong() {
        assertThatThrownBy(() -> 구간.modifyBy(new Section(line, new Station("중간역"), 하행선, DEFAULT_DISTANCE)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("두 구간을 합친다")
    @Test
    void merge() {
        Station 신규하행선 = new Station("신규하행선");
        Section newSection = new Section(line, 하행선, 신규하행선, 3);
        구간.merge(newSection);
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(구간.getUpStation()).isEqualTo(상행선);
            softAssertions.assertThat(구간.getDownStation()).isEqualTo(신규하행선);
            softAssertions.assertThat(구간.getDistance()).isEqualTo(DEFAULT_DISTANCE + 3);
        });
    }
}
