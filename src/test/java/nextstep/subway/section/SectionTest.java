package nextstep.subway.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionTest {

    @DisplayName("구역 생성 검증")
    @Test
    void create() {
        Section section = new Section(1L,2L,10);
        assertThat(section).isNotNull();
    }
}
