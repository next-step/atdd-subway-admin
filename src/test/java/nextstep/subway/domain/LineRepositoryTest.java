package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LineRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private LineRepository lineRepository;

    private Line line1;
    private Line line2;

    @BeforeEach
    void setUp() {
        Station upStation = new Station("상행");
        testEntityManager.persistAndFlush(upStation);

        Station downStation = new Station("하행");
        testEntityManager.persistAndFlush(downStation);

        line1 = new Line("1호선", "bg-red-600", 10, upStation, downStation);
        testEntityManager.persistAndFlush(line1);

        line2 = new Line("2호선", "bg-green-600", 10, upStation, downStation);
        testEntityManager.persistAndFlush(line2);

        testEntityManager.clear();
    }

    @Test
    @DisplayName("전체 노선을 가져올 때, 각 노선의 모든 구간 정보까지 가져올 수 있다.")
    void findAll() {
        List<Line> lines = lineRepository.findAll();
        assertThat(lines).allMatch(it -> !it.getLineStations().isEmpty());
    }

    @Test
    @DisplayName("한 노선을 가져올 때, 이 노선의 모든 구간 정보까지 가져올 수 있다.")
    void findOne() {
        Line line = lineRepository.findById(line1.getId()).orElseThrow(NoSuchElementException::new);
        assertThat(line.getLineStations()).isNotEmpty();
    }

}
