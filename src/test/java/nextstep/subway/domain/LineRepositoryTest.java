package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LineRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private LineRepository lineRepository;

    private Line line;

    @BeforeEach
    void setUp() {
        Station upStation = new Station("상행");
        testEntityManager.persistAndFlush(upStation);

        Station downStation = new Station("하행");
        testEntityManager.persistAndFlush(downStation);

        line = new Line("신분당선", "bg-red-600", upStation, downStation, 10L);
        testEntityManager.persistAndFlush(line);

        testEntityManager.clear();
    }

    @Test
    @DisplayName("전체 노선을 가져올 때, 각 노선의 상행/하행 역 정보까지 가져올 수 있다.")
    void findAll() {
        List<Line> lines = lineRepository.findAll();
        assertThat(lines).containsExactly(line);
    }

}
