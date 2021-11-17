package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionTest {

    private static final Line 이호선 = new Line("2호선", "green");
    private static final Station 강남역 = new Station("강남역");
    private static final Station 역삼역 = new Station("역삼역");
    private static final Station 삼성역 = new Station("삼성역");

    @Test
    void update_새로운_구간으로_업데이트_한다() {
        Section 구간 = new Section(이호선, 강남역, 삼성역, 10);
        구간.update(역삼역, 3);
        assertThat(구간).isEqualTo(new Section(이호선, 역삼역, 삼성역, 7));
    }
}
