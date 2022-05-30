package nextstep.subway.domain;

import nextstep.subway.exception.LineNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LineRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private LineRepository repository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Test
    void 생성() {
        // when
        Station upStation = stationRepository.save(new Station("잠실역"));
        Station downStation = stationRepository.save(new Station("강남역"));
        Line saved = repository.save(new Line("2호선", "#009D3E", 100, upStation, downStation));

        // then
        assertThat(saved).isNotNull();
    }

    @Test
    void 조회() {
        // given
        Station upStation = stationRepository.save(new Station("잠실역"));
        Station downStation = stationRepository.save(new Station("강남역"));
        Line saved = repository.save(new Line("2호선", "#009D3E", 100, upStation, downStation));

        // when
        Optional<Line> line = repository.findById(saved.getId());

        // then
        assertThat(line.isPresent()).isTrue();
    }

    @Test
    void 수정() {
        // given
        Station upStation = stationRepository.save(new Station("잠실역"));
        Station downStation = stationRepository.save(new Station("강남역"));
        Line saved = repository.save(new Line("2호선", "#009D3E", 100, upStation, downStation));

        // when
        saved.updateLine("1호선", "파랑색");

        // then
        Optional<Line> line = repository.findById(saved.getId());
        assertThat(line.get().getName()).isEqualTo("1호선");
        assertThat(line.get().getColor()).isEqualTo("파랑색");
    }

    @Test
    void 삭제() {
        // given
        Station upStation = stationRepository.save(new Station("잠실역"));
        Station downStation = stationRepository.save(new Station("강남역"));
        Line line = repository.save(new Line("2호선", "#009D3E", 100, upStation, downStation));

        // when
        repository.delete(line);

        // then
        Optional<Line> actual = repository.findById(line.getId());
        assertThat(actual.isPresent()).isFalse();
    }

    @Test
    void 상행종점역_하행종점역_조회() {
        // given
        Station upStation = stationRepository.save(new Station("잠실역"));
        Station downStation = stationRepository.save(new Station("강남역"));
        Line line = repository.save(new Line("2호선", "#009D3E", 100, upStation, downStation));
        entityManager.clear();

        // when
        Optional<Line> actual = repository.findById(line.getId());

        // then
        assertThat(actual.get().getUpStation()).isEqualTo(upStation);
        assertThat(actual.get().getDownStation()).isEqualTo(downStation);
    }
}
