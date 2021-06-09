package nextstep.subway.line.domain;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    @DisplayName("노선의 업데이트 기능 테스트")
    @Test
    void updateLine() {
        Line 노선 = new Line("2호선", "초록색");

        Line 업데이트할노선 = new Line("2호선", "진한초록색");
        노선.update(업데이트할노선);

        assertThat(노선.getColor()).isEqualTo(업데이트할노선.getColor());
    }

    @DisplayName("구간정보를 노선에 추가했을때 기능테스트")
    @Test
    void addSection() {
        Line 노선 = new Line("2호선", "초록색");

        Station 상행선 = new Station("강남역");
        Station 하행선 = new Station("잠실역");

        Section 구간 = Section.create(상행선, 하행선, 200);
        노선.addSection(구간);
        assertThat(노선.getSections().getSections()).containsExactly(구간);
    }
}