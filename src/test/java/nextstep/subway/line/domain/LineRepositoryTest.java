package nextstep.subway.line.domain;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class LineRepositoryTest {
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void create() {
        Line actual = new Line("2호선");
        lineRepository.save(actual);
        entityManager.clear();

        Line expected = lineRepository.findById(1L)
                .orElseThrow(RuntimeException::new);

        assertThat(actual).isEqualTo(expected);
    }
}
