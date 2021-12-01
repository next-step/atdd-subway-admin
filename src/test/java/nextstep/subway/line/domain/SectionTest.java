package nextstep.subway.line.domain;

import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionTest {

    @Test
    @DisplayName("기존 구간과 새로운 구간을 수정한다.")
    void updateSection() {
        // given
        Station 강남역 = new Station("강남역");
        Station 판교역 = new Station("판교역");
        Station 양재역 = new Station("양재역");

        int 강남_판교_길이 = 10;
        int 강남_양재_길이 = 3;
        Section 강남_판교_구간 = new Section(강남역, 판교역, 강남_판교_길이);
        Section 강남_양재_구간 = new Section(강남역, 양재역, 강남_양재_길이);

        // when
        강남_판교_구간.updateUpSection(강남_양재_구간);

        // then
        assertThat(강남_판교_구간.getUpStation()).isEqualTo(양재역);
        assertThat(강남_판교_구간.getDistance().getDistance()).isEqualTo(강남_판교_길이 - 강남_양재_길이);
    }
}
