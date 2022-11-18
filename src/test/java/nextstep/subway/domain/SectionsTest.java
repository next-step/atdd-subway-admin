package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    @Test
    @DisplayName("구간 추가 성공")
    void add() {
        // given
        Station upStation = Station.of(1L, "잠실역");
        Station downStation = Station.of(2L, "장지역");
        Integer distance = 10;
        Section section = Section.of(upStation, downStation, distance);
        Sections sections = Sections.from(Collections.singletonList(section));

        // given
        Station addStation = Station.of(3L, "문정역");
        Integer addDistance = 5;
        Section addSection = Section.of(upStation, addStation, addDistance);

        // when
        sections.add(addSection);

        // then
        assertThat(sections.size()).isEqualTo(2);
    }
}
