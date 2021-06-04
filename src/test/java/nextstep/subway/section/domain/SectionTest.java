package nextstep.subway.section.domain;

import static nextstep.subway.station.domain.StationTest.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionTest {

    @Test
    @DisplayName("생성 테스트")
    void create() {
        // given
        Section section = new Section(강남역, 판교역, 5);
        // when & then
        assertThat(section.getUpStation()).isEqualTo(강남역);
        assertThat(section.getDownStation()).isEqualTo(판교역);
        assertThat(section.getDistance()).isEqualTo(5);
    }
}
