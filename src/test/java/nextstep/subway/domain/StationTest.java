package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class StationTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private TestEntityManager entityManager;


    @DisplayName("지하철역 생성 테스트")
    @Test
    void 지하철역생성_성공() {
        // given
        Station sinsa = new Station("신사역");

        // when
        stationRepository.save(sinsa);
        flushAndClear();
        Station station = stationRepository.findById(1L).get();

        // then
        assertThat(station).isEqualTo(sinsa);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
