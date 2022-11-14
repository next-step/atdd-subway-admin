package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class LineTest {
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private TestEntityManager entityManager;
    private List<Station> stations;

    @BeforeEach
    void setUp() {
        stations = Arrays.asList(new Station("신사역"), new Station("광교(경기대)역"));
        stationRepository.saveAll(stations);
        flushAndClear();
    }

    @DisplayName("라인생성 테스트")
    @Test
    void 생성() {
        // given
        Line shinbundang = new Line("신분당선", "bg-red-600", stations, 10);

        // when
        lineRepository.save(shinbundang);
        flushAndClear();
        Line line = lineRepository.findById(1L).get();

        // then
        assertThat(line).isEqualTo(shinbundang);
    }

    @DisplayName("라인을 생성하고 addStation 메소드 테스트")
    @Test
    void 라인생성_성공_addStation테스트() {
        // given
        Line shinbundang = new Line("신분당선", "bg-red-600", 10);
        Station gangNam = new Station("강남역");
        Station seongSoo = new Station("성수역");
        stationRepository.saveAll(Arrays.asList(gangNam, seongSoo));

        // when
        shinbundang.addStation(gangNam);
        shinbundang.addStation(seongSoo);
        lineRepository.save(shinbundang);
        flushAndClear();

        // then
        Line line = lineRepository.findById(1L).get();
        assertThat(line.getStations()).contains(gangNam);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
