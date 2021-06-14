package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class SectionRepositoryTest {

    @Autowired
    StationRepository stationRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Test
    @DisplayName("라인과 상행,하행을 만들어서 섹션값과 맞는지 확인한다.")
    void sectionExceptionTest() {
        // given
        Line line = new Line("2호선", "color-green");
        Station upStation = stationRepository.save(new Station("강남역")); // 상행
        Station downStation = stationRepository.save(new Station("역삼역")); // 하행

        // when
        Section section = sectionRepository.save(new Section(line, upStation, downStation, 5L));

        // then
        assertAll(
                () -> assertThat(section).isNotNull(),
                () -> assertThat(section.getUpStation().getName()).isEqualTo(upStation.getName()),
                () -> assertThat(section.getDownStation().getName()).isEqualTo(downStation.getName()),
                () -> assertThat(section.getDistance().get()).isEqualTo(5L)
        );

    }
}