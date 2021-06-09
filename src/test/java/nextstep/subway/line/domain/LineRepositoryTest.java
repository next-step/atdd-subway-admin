package nextstep.subway.line.domain;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class LineRepositoryTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private LineRepository lineRepository;

    @Test
    @DisplayName("저장된 섹션을 라인에 셋팅해서 저장 해야 한다.")
    void save_line_with_section() {
        // given
        Station upStation = stationRepository.save(new Station("강남역"));
        Station downStation = stationRepository.save(new Station("역삼역"));

        Line line = new Line("2호선", "color-green", upStation, downStation, 5L);
        Line saveLine = lineRepository.save(line);

        // 섹션을 만들어서 상행선, 하행선, 그리고 거리를 저장한다.
        Section section = sectionRepository.save(new Section(saveLine, upStation, downStation, 5L));

        // then
        assertAll(
                () -> assertThat(section).isNotNull(),
                () -> assertThat(section.getStations()).isEqualTo(saveLine.getStations())
        );
    }
}