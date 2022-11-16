package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LineTest {
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private TestEntityManager entityManager;
    private Station upStation;
    private Station downStation;

    @BeforeEach
    void setUp() {
        upStation = new Station("신사역");
        downStation = new Station("광교(경기대)역");
        stationRepository.saveAll(Arrays.asList(upStation, downStation));
        flushAndClear();
    }

    @DisplayName("라인생성 테스트")
    @Test
    void 생성() {
        // given
        Line shinbundang = new Line("신분당선", "bg-red-600", upStation, downStation, 10);

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
        Station gangNam = new Station("강남역");
        Station seongSoo = new Station("성수역");
        stationRepository.saveAll(Arrays.asList(gangNam, seongSoo));

        // when
        Line line = new Line("신분당선", "bg-red-600", upStation, downStation, 10);
        line.setUpStation(gangNam);
        line.setDownStation(seongSoo);
        lineRepository.save(line);
        flushAndClear();

        // then
        Line responseLine = lineRepository.findById(1L).get();
        assertAll(
                () -> assertThat(responseLine.getUpStation()).isEqualTo(gangNam),
                () -> assertThat(responseLine.getDownStation()).isEqualTo(seongSoo)
        );
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
