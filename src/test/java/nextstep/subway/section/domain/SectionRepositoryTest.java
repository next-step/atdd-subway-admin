package nextstep.subway.section.domain;


import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class SectionRepositoryTest{

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @DisplayName("구역  생성 검증")
    @Test
    void createSection() {
        Station upStation = stationRepository.save(new Station("건대역"));
        Station downStation = stationRepository.save(new Station("용마산역"));
        Line line = lineRepository.save(new Line("bg-red-600", "7호선"));

        Section section = sectionRepository.save(Section.of(line, upStation, downStation, 10));

        assertAll(
                () -> assertThat(section).isNotNull(),
                () -> assertThat(section.getDistance()).isEqualTo(10)

        );
    }
}
