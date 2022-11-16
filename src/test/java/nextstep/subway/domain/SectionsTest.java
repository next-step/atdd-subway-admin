package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionsTest {

    @Test
    @DisplayName("구간 추가 성공")
    void add() {
        // given
        Station upStation = Station.from("잠실역");
        Station downStation = Station.from("장지역");
        Integer distance = 10;
        Section section = Section.of(upStation, downStation, distance);
        Sections sections = Sections.from(Collections.singletonList(section));

        // given
        Station addStation = Station.from("문정역");
        Integer addDistance = 10;
        Section addSection = Section.of(upStation, addStation, addDistance);

        // when
        sections.add(addSection);

        // then
        assertThat(sections.size()).isEqualTo(2);
    }
}
