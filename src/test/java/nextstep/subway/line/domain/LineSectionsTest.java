package nextstep.subway.line.domain;

import static nextstep.subway.section.domain.SectionTest.*;
import static nextstep.subway.station.domain.StationTest.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.section.domain.Section;

public class LineSectionsTest {
    public final LineSections lineSections = new LineSections(Arrays.asList(강남_판교_구간, 판교_수지_구간, 수지_광교_구간));

    @Test
    @DisplayName("구간 내 역 정보 반환 테스트")
    void toStationsTest() {
        // given & when & then
        assertThat(lineSections.toStations()).isEqualTo(Arrays.asList(강남역, 판교역, 판교역, 수지역, 수지역, 광교역));
    }

    @Test
    @DisplayName("구간 내 역 정렬 테스트")
    void orderedLineStationsTest() {
        // given
        LineSections notOrderedLineSections = new LineSections(Arrays.asList(판교_수지_구간, 수지_광교_구간, 강남_판교_구간));
        // when
        List<Section> orderLineSections = notOrderedLineSections.getOrderLineSections();
        // then
        assertThat(orderLineSections).isEqualTo(lineSections.getSections());
    }
}
