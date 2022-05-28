package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {
    private final Line twoLine = new Line("이호선", "노란색");
    private final Station gangNam = new Station("강남역");
    private final Station seoCho = new Station("서초역");

    @Test
    @DisplayName("두 객체가 같은지 검증")
    void verifySameSectionObject() {
        Section section = new Section(twoLine, gangNam, seoCho, 10L);

        assertThat(section).isEqualTo(new Section(twoLine, gangNam, seoCho, 10L));
    }
}
