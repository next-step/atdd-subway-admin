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
    private final Station aStation = new Station("잠실역");
    private final Station bStation = new Station("강남역");
    private final Line aLine = new Line("2호선", "#009D3E", 100);

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
        Line saved = repository.save(aLine);

        // then
        assertThat(saved).isNotNull();
    }

    @Test
    void 조회() {
        // given
        Line saved = repository.save(aLine);

        // when
        Optional<Line> line = repository.findById(saved.getId());

        // then
        assertThat(line.isPresent()).isTrue();
    }

    @Test
    void 수정() {
        // given
        Line saved = repository.save(aLine);

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
        Line line = repository.save(aLine);

        // when
        repository.delete(line);

        // then
        Optional<Line> actual = repository.findById(line.getId());
        assertThat(actual.isPresent()).isFalse();
    }

    @Test
    void 상행종점역_하행종점역_조회() {
        // given
        Station upStation = stationRepository.save(aStation);
        Station downStation = stationRepository.save(bStation);
        Line line = repository.save(aLine).setUpStation(upStation).setDownStation(downStation);

        // when
        Optional<Line> actual = repository.findById(line.getId());

        // then
        assertThat(actual.get().getUpStation()).isEqualTo(upStation);
        assertThat(actual.get().getDownStation()).isEqualTo(downStation);
    }

    @Test
    void 노선내_연결정보_가져오기() {
        // given
        Station upStation = stationRepository.save(aStation);
        Station downStation = stationRepository.save(bStation);
        Line line = repository.save(aLine).setUpStation(upStation).setDownStation(downStation);
        sectionRepository.save(new Section(100, upStation, downStation, aLine));
        entityManager.clear();

        // when
        Line actual = repository.findById(line.getId())
                .orElseThrow(LineNotFoundException::new);

        // then
        assertThat(actual.getSections()).isNotNull();
        assertThat(actual.getSections().getList()).hasSize(1);
    }
}
