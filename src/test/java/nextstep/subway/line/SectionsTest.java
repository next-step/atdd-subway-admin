package nextstep.subway.line;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayName("노선에 속한 구간 관련 테스트")
class SectionsTest {
    private static final int DEFAULT_DISTANCE = 10;

    Station 상행종점역;
    Station 하행종점역;
    Line 노선;

    @DisplayName("상행종점역과 하행종점역을 갖는 노선을 추가한다.")
    @BeforeEach
    void setUp() {
        상행종점역 = new Station("상행종점역");
        하행종점역 = new Station("하행종점역");
        노선 = new Line("2호선", "green");
        노선.addSection(상행종점역, 하행종점역, DEFAULT_DISTANCE);
    }

    @DisplayName("구간 중간에 구간을 추가한다.")
    @Test
    void addSectionLeft() {
        Station 중간역 = new Station("중간역");
        노선.addSection(상행종점역, 중간역, 3);
        assertThat(노선.stations()).containsSequence(상행종점역, 중간역, 하행종점역);
    }

    @DisplayName("구간 중간에 구간을 추가한다.")
    @Test
    void addSectionRight() {
        Station 중간역 = new Station("중간역");
        노선.addSection(중간역, 하행종점역, 3);
        assertThat(노선.stations()).containsSequence(상행종점역, 중간역, 하행종점역);
    }

    @DisplayName("상행 종점구간을 추가한다.")
    @Test
    void addSectionBegin() {
        Station 신규상행종점역 = new Station("신규상행종점역");
        노선.addSection(신규상행종점역, 상행종점역, 3);
        assertThat(노선.stations()).containsSequence(신규상행종점역, 상행종점역, 하행종점역);
    }

    @DisplayName("하행 종점구간을 추가한다.")
    @Test
    void addSectionEnd() {
        Station 신규하행종점역 = new Station("신규하행종점역");
        노선.addSection(하행종점역, 신규하행종점역, 3);
        assertThat(노선.stations()).containsSequence(상행종점역, 하행종점역, 신규하행종점역);
    }

    @DisplayName("여러 구간을 복합적으로 추가한다.")
    @Test
    void addSections() {
        Station 서초역 = new Station("서초역");
        Station 논현역 = new Station("논현역");
        Station 신도림역 = new Station("신도림역");
        Station 신림역 = new Station("신림역");
        노선.addSection(상행종점역, 서초역, 3);
        노선.addSection(서초역, 논현역, 4);
        노선.addSection(하행종점역, 신도림역, 4);
        노선.addSection(신림역, 상행종점역, 1);
        assertThat(노선.stations()).containsSequence(신림역, 상행종점역, 서초역, 논현역, 하행종점역, 신도림역);
    }

    @DisplayName("구간 추가 시 기존 역 사이보다 길거나 같으면 등록할 수 없다")
    @Test
    void addTooLongSection() {
        Station 중간역 = new Station("중간역");
        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> 노선.addSection(상행종점역, 중간역, DEFAULT_DISTANCE))
                    .isInstanceOf(IllegalArgumentException.class);
            softAssertions.assertThatThrownBy(() -> 노선.addSection(상행종점역, 중간역, DEFAULT_DISTANCE + 1))
                    .isInstanceOf(IllegalArgumentException.class);
        });
    }

    @DisplayName("등록하는 구간의 상행역과 하행역이 이미 등록되어 있다면 등록할 수 없다")
    @Test
    void addSameSection() {
        assertThatThrownBy(() -> 노선.addSection(상행종점역, 하행종점역, 3))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("등록하는 구간의 상행역과 하행역이 하나도 등록되어있지 않다면 등록할 수 없다")
    @Test
    void addUnknownSection() {
        assertThatThrownBy(() -> 노선.addSection(new Station("신규역1"), new Station("신규역2"), 3))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행종점역 삭제")
    @Test
    void removeBeginSection() {
        Station 인근역 = new Station("인근역");
        노선.addSection(상행종점역, 인근역, 3);
        노선.removeSection(상행종점역);
        assertThat(노선.stations()).containsExactly(인근역, 하행종점역);
    }

    @DisplayName("하행종점역 삭제")
    @Test
    void removeEndSection() {
        Station 인근역 = new Station("인근역");
        노선.addSection(인근역, 하행종점역, 3);
        노선.removeSection(하행종점역);
        assertThat(노선.stations()).containsExactly(상행종점역, 인근역);
    }

    @DisplayName("중간역 삭제")
    @Test
    void removeMidStation() {
        Station 중간역 = new Station("중간역");
        노선.addSection(상행종점역, 중간역, 3);
        노선.removeSection(중간역);
        assertThat(노선.stations()).containsExactly(상행종점역, 하행종점역);
    }

    @DisplayName("노선의 마지막 구간은 삭제할 수 없다.")
    @Test
    void removeLastSection() {
        assertThatThrownBy(() -> 노선.removeSection(상행종점역))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
