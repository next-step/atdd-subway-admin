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

    @DisplayName("구간의 길이를 줄인다.")
    @Test
    void minusDistance() {
        // given
        Station upStation = new Station("테스트 상행역");
        Station downStation  = new Station("테스트 하행역");
        Distance distance = Distance.valueOf(40);

        Station minusingUpStation = new Station("삭제 상행역");
        Station minusingDownStation  = new Station("삭제 하행역");
        Distance minusingDistance = Distance.valueOf(15);

        // when
        Section createdSection = Section.valueOf(upStation, downStation, distance);
        createdSection.minusDistance(Section.valueOf(minusingUpStation, minusingDownStation, minusingDistance));

        // then
        assertAll("validSection",
            () -> Assertions.assertThat(createdSection.getUpStation()).isEqualTo(upStation),
            () -> Assertions.assertThat(createdSection.getDownStation()).isEqualTo(downStation),
            () -> Assertions.assertThat(createdSection.getDistance().value()).isEqualTo(25)
        );
    }

    @DisplayName("기등록된 구간보다 긴 길이로 뺄경우 예외를 발생시킨다.")
    @Test
    void valid_minusDistanceOverPrevDistance() {
        // given
        Station upStation = new Station("테스트 상행역");
        Station downStation  = new Station("테스트 하행역");
        Distance distance = Distance.valueOf(40);

        Station minusingUpStation = new Station("삭제 상행역");
        Station minusingDownStation  = new Station("삭제 하행역");
        Distance minusingDistance = Distance.valueOf(55);

        // when
        Section createdSection = Section.valueOf(upStation, downStation, distance);
        
        
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> createdSection.minusDistance(Section.valueOf(minusingUpStation, minusingDownStation, minusingDistance)));
    }

    @DisplayName("구간의 길이를 늘린다.")
    @Test
    void plusDistance() {
        // given
        Station upStation = new Station("테스트 상행역");
        Station downStation  = new Station("테스트 하행역");
        Distance distance = Distance.valueOf(20);
        
        Station plusingUpStation = new Station("추가 상행역");
        Station plusingDownStation  = new Station("추가 하행역");
        Distance plusingDistance = Distance.valueOf(10);

        // when
        Section createdSection = Section.valueOf(upStation, downStation, distance);
        createdSection.plusDistance(Section.valueOf(plusingUpStation, plusingDownStation, plusingDistance));

        // then
        assertAll("validSection",
            () -> Assertions.assertThat(createdSection.getUpStation()).isEqualTo(upStation),
            () -> Assertions.assertThat(createdSection.getDownStation()).isEqualTo(downStation),
            () -> Assertions.assertThat(createdSection.getDistance().value()).isEqualTo(30)
        );
    }
}
