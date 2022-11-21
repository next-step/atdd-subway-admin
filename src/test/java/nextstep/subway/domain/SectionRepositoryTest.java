package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(DatabaseCleanup.class)
public class SectionRepositoryTest {

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @Autowired
    private SectionRepository sectionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Station upStation;
    private Station downStation;
    private Line line;

    @BeforeEach
    void setUp() {
        databaseCleanup.execute();
        // given
        upStation = Station.from("잠실역");
        downStation = Station.from("문정역");
        line = Line.of("8호선", "분홍색", upStation, downStation, 10);
        entityManager.persist(line);
        entityManager.persist(upStation);
        entityManager.persist(downStation);
    }


    @Test
    @DisplayName("노선 아이디와 상행역 아이디로 구간 검색")
    void findByLineIdAndUpStationId() {
        // when
        Optional<Section> section = sectionRepository.findByLineIdAndUpStationId(1L, upStation.getId());

        // then
        assertThat(section).isPresent();
    }

    @Test
    @DisplayName("노선 아이디와 하행역 아이디로 구간 검색")
    void findByLineIdAndDownStationId() {
        // when
        Optional<Section> section = sectionRepository.findByLineIdAndDownStationId(1L, downStation.getId());

        // then
        assertThat(section).isPresent();
    }
}
