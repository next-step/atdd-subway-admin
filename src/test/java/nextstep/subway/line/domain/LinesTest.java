package nextstep.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class LinesTest {
    @DisplayName("지하철 노선 목록 일급 컬렉션 생성")
    @Test
    void test_create() {
        // given
        Line newLine = new Line("신분당선", "bg-red-600");
        // when
        Lines lines = Lines.from(Arrays.asList(newLine));
        // then
        assertThat(lines).isNotNull();
    }
}