package nextstep.subway.section;

import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class SectionRepositoryTest {
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private SectionRepository sectionRepository;

    private Station upStation;
    private Station downStation;
    private Line line;

    @BeforeEach
    void setUp() {
        upStation = stationRepository.save(new Station("강남역"));
        downStation = stationRepository.save(new Station("역삼역"));
        line = lineRepository.save(new Line("신분당선", "bg-red-600", upStation, downStation));
    }

    @Test
    void 지하철_구간_저장() {
        Section section = new Section(line, upStation, new Station("양재역"), 5);
        Section result = sectionRepository.save(section);

        assertAll(
                () -> assertThat(result.getId()).isNotNull(),
                () -> assertThat(result.getLine()).isEqualTo(section.getLine()),
                () -> assertThat(result.getUpStation()).isEqualTo(section.getUpStation()),
                () -> assertThat(result.getDownStation()).isEqualTo(section.getDownStation()),
                () -> assertThat(result.getDistance()).isEqualTo(section.getDistance())
        );
    }
}
