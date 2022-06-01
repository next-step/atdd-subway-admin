package nextstep.subway.section.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class LineStationsTest {
    @DisplayName("지하철역 목록 일급 컬렉션 생성")
    @Test
    void test_create() {
        // given & when
        LineStations lineStations = LineStations.from(Arrays.asList(SectionTest.강남역));
        // then
        assertThat(lineStations).isNotNull();
        assertThat(lineStations.contains(SectionTest.강남역)).isNotNull();
    }
}