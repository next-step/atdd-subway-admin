package nextstep.subway.line.domain;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class LineRepositoryTest {
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private EntityManager entityManager;

    @DisplayName("지하철 노선 생성")
    @Test
    void create() {
        Line actual = new Line("2호선");
        lineRepository.save(actual);
        entityManager.clear();

        Line expected = lineRepository.findById(1L)
                .orElseThrow(RuntimeException::new);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("지하철 노선 조회")
    @Test
    void getLines() {
        List<Line> lines = Arrays.asList(new Line("2호선"), new Line("1호선"));
        lineRepository.saveAll(lines);
        entityManager.clear();

        List<Line> actual = lineRepository.findAll();

        assertThat(actual.size()).isEqualTo(2);
        assertThat(actual).containsExactlyElementsOf(lines);
    }
}
