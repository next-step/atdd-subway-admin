package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("LineRepository 테스트")
@DataJpaTest
class LineRepositoryTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Test
    @DisplayName("전체 지하철 노선 목록을 조회")
    void findAll() {
        Station upStation = stationRepository.save(new Station("강남역"));
        Station downStation = stationRepository.save(new Station("양재역"));

        Line line = new Line("신분당선", "bg-red-600");
        new Section(line, upStation, downStation, 10);
        lineRepository.save(line);

        List<Line> lines =  lineRepository.findAll();

        assertAll(
                () -> assertThat(lines).hasSize(1),
                () -> assertEquals("신분당선", lines.get(0).getName()),
                () -> assertThat(lines.get(0).getSections()).hasSize(1)
        );
    }
}
