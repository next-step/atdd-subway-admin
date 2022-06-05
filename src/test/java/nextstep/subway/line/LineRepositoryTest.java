package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class LineRepositoryTest {

    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StationRepository stationRepository;

    @Test
    public void save() {
        //given
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 판교역 = stationRepository.save(new Station("판교역"));

        Line expected = new Line("신분당선", "bg-red-600", 강남역, 판교역, 10l);
        //when
        Line actual = lineRepository.save(expected);
        //then
        assertThat(actual).isEqualTo(expected);
    }
}