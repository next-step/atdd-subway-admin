package nextstep.subway.section;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SectionTest {

    @DisplayName("구간 생성")
    @Test
    void saveSection() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        int distance = 10;
        //when
        Section section = new Section(강남역, 역삼역, distance);
        //then
        assertAll(
                () -> assertThat(section.getUpStation()).isEqualTo(강남역),
                () -> assertThat(section.getDownStation()).isEqualTo(역삼역),
                () -> assertThat(section.getDistance()).isEqualTo(distance)
        );

    }
}
