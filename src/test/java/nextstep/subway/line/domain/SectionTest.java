package nextstep.subway.line.domain;

import static org.junit.jupiter.api.Assertions.assertAll;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

@DisplayName("구간 관련 기능")
public class SectionTest {
    static Section section_distant100 = Section.valueOf(new Station("테스트 상행역1"), new Station("테스트 하행역1"), Distance.valueOf(100));
    static Section section_distant80 = Section.valueOf(new Station("테스트 상행역2"), new Station("테스트 하행역2"), Distance.valueOf(80));
    static Section section_distant50 = Section.valueOf(new Station("테스트 상행역3"), new Station("테스트 하행역3"), Distance.valueOf(50));
    static Section section_distant30 = Section.valueOf(new Station("테스트 상행역4"), new Station("테스트 하행역4"), Distance.valueOf(30));
    static Section section_distant10 = Section.valueOf(new Station("테스트 상행역5"), new Station("테스트 하행역5"), Distance.valueOf(10));

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
