package nextstep.subway.section.domain;

import static org.junit.jupiter.api.Assertions.assertAll;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

@DisplayName("구간 관련 기능")
public class SectionTest {
    @DisplayName("구간을 생성한다.")
    @Test
    void create() {
        // given
        Station upStation = new Station("테스트 상행역");
        Station downStation  = new Station("테스트 하행역");
        Distance distance = Distance.valueOf(20);

        // when
        Section createdSection = Section.valueOf(upStation, downStation, distance);

        // then
        assertAll("validSection",
            () -> Assertions.assertThat(createdSection.getUpStation()).isEqualTo(upStation),
            () -> Assertions.assertThat(createdSection.getDownStation()).isEqualTo(downStation),
            () -> Assertions.assertThat(createdSection.getDistance()).isEqualTo(distance)
        );
    }
}
