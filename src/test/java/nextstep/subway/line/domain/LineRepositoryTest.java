package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

/**
 * LineRepository 기능 테스트
 */
@DataJpaTest
class LineRepositoryTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private LineRepository lineRepository;

    private Station firstStation;
    private Station secondStation;
    private Station thirdStation;
    private List<Station> stations;
    private Section firstSection;
    private Section secondSection;

    @BeforeEach
    void setUp() {
        this.firstStation = new Station("강남역");
        this.secondStation = new Station("역삼역");
        this.thirdStation = new Station("교대역");
        this.stations = stationRepository.saveAll(Arrays.asList(this.firstStation, this.secondStation, this.thirdStation));
        this.firstSection = new Section(this.firstStation, this.secondStation, 10);
        this.secondSection = new Section(this.secondStation, this.thirdStation, 10);
    }

    @Test
    @DisplayName("구간 추가된 노선 저장")
    void new_create_line() {
        // given
        Line line = new Line("2호선", "green");
        line.addSection(this.firstSection);

        // when
        Line savedLine = lineRepository.save(line);

        // then
        assertThat(savedLine).isSameAs(line);
    }
}
