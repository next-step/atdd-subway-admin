package nextstep.subway.line;

import static java.util.Arrays.*;

import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@DataJpaTest
public class LineRepositoryTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Test
    void 지하철역_다건_조회() {
        // given
        Station station1 = new Station("강남역");
        Station station2 = new Station("서울역");
        List<Station> stations = stationRepository.saveAll(asList(station1, station2));
        List<Long> stationId = stations.stream().map(Station::getId).collect(Collectors.toList());
        // when
        List<Station> findStations = stationRepository.findAllByIdIsIn(stationId);

        // then
        Assertions.assertThat(findStations).containsExactly(station1, station2);
    }

    @Test
    void 지하철_라인_외래키_참조확인() {
        // given
        Station station1 = new Station("강남역");
        List<Station> stations = stationRepository.saveAll(asList(station1));

        // then
        Assertions.assertThatThrownBy(() -> {
            Line line = new Line("2호선", "bg-color-red", new Section(stations.get(0).getId(), 2L));
            lineRepository.save(line);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }
}
