package nextstep.subway.domain;

import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(DatabaseCleanup.class)
public class StationTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        databaseCleanup.execute();
    }

    @DisplayName("지하철역 생성 테스트")
    @Test
    void 지하철역생성_성공() {
        // given
        Station sinsa = new Station("신사역");

        // when
        stationRepository.save(sinsa);
        flushAndClear();

        // then
        assertThat(stationRepository.findById(1L)).get().isEqualTo(sinsa);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
