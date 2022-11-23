package nextstep.subway.domain;

import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import javax.persistence.NoResultException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(DatabaseCleanup.class)
public class LineTest {
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private DatabaseCleanup databaseCleanup;
    private Station upStation;
    private Station downStation;
    private Station newStation;

    @BeforeEach
    void setUp() {
        databaseCleanup.execute();
        upStation = new Station("판교역");
        downStation = new Station("경기광주역");
        newStation = new Station("이매역");

        stationRepository.saveAll(Arrays.asList(upStation, downStation, newStation));
        flushAndClear();
    }

    @DisplayName("라인생성 테스트")
    @Test
    void 생성() {
        // given
        Line shinbundang = new Line("신분당선", "bg-red-600");

        // when
        lineRepository.save(shinbundang);
        flushAndClear();

        // then
        assertThat(lineRepository.findById(1L)).get().isEqualTo(shinbundang);
    }

    @DisplayName("라인을 생성한 후, 수정된 값으로 업데이트 한다")
    @Test
    void 라인수정_성공() {
        // given
        Line line = new Line("신분당선", "bg-red-600");
        line.connect(upStation, downStation, 100);
        lineRepository.save(line);
        flushAndClear();

        // when
        Line newLine = lineRepository.findById(1L).orElseThrow(NoResultException::new);
        newLine.update(new Line("경기광주선", "bg-blue-100"));
        flushAndClear();

        // then
        assertThat(newLine.getName()).isEqualTo("경기광주선");
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
