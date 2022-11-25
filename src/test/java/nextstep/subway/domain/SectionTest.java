package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class SectionTest {
    private Station upStation;
    private Station downStation;
    private Line line;

    @BeforeEach
    void setUp() {
        upStation = new Station("강남역");
        downStation = new Station("판교역");
        line = new Line("신분당선", "red");
    }

    @DisplayName("거리가 10인 구간을 생성한다.")
    @Test
    void create() {
        Section section = new Section(upStation, downStation, new Distance(10));
        assertAll(() -> {
            assertThat(section.getUpStation()).isEqualTo(new Station("강남역"));
            assertThat(section.getDownStation()).isEqualTo(new Station("판교역"));
            assertThat(section.getDistance().value()).isEqualTo(10);
        });
    }

    @DisplayName("구간에 노선을 추가한다.")
    @Test
    void addLine() {
        Section section = new Section(upStation, downStation, new Distance(10));
        section.addLine(line);
        assertThat(section.getLine().getName()).isEqualTo("신분당선");
        assertThat(section.getLine().getColor()).isEqualTo("red");
    }

    @DisplayName("구간에 상행, 하행역이 하나라도 포함되어 있으면 true")
    @Test
    void contains() {
        Section section = new Section(upStation, downStation, new Distance(10));
        assertTrue(section.contains(upStation));
        assertTrue(section.contains(downStation));
    }

    @DisplayName("구간에 상행, 하행역이 하나라도 포함되어 있지 않으면 false")
    @Test
    void not_contains() {
        Section section = new Section(upStation, downStation, new Distance(10));
        assertFalse(section.contains(new Station("여의도역")));
        assertFalse(section.contains(new Station("사당역")));
    }

    @DisplayName("구간에서 상행역이 일치하면 true")
    @Test
    void equalsUpStation() {
        Section section = new Section(upStation, downStation, new Distance(10));
        assertTrue(section.equalsUpStation(new Station("강남역")));
    }

    @DisplayName("구간에서 상행역이 일치하면 true")
    @Test
    void equalsDownStation() {
        Section section = new Section(upStation, downStation, new Distance(10));
        assertTrue(section.equalsDownStation(new Station("판교역")));
    }

    @DisplayName("구간 거리를 뺀다.")
    @Test
    void minusDistance() {
        Section section = new Section(upStation, downStation, new Distance(10));
        section.minusDistance(4);
        assertThat(section.getDistance().value()).isEqualTo(6);
    }

    @DisplayName("구간의 상행, 하행역을 수정한다.")
    @Test
    void updateStation() {
        Section section = new Section(upStation, downStation, new Distance(10));
        section.updateUpStation(new Station("여의도역"));
        section.updateDownStation(new Station("당산역"));
        assertThat(section.getUpStation()).isEqualTo(new Station("여의도역"));
        assertThat(section.getDownStation()).isEqualTo(new Station("당산역"));
    }
}
