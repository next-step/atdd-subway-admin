package nextstep.subway.line.domain;


import nextstep.subway.line.exception.LineNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
public class LineRepositoryTest {
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private EntityManager entityManager;

    @DisplayName("지하철 노선 생성한다")
    @Test
    void create() {
        Line actual = new Line("2호선");
        lineRepository.save(actual);
        entityManager.clear();

        Line expected = lineRepository.findById(1L)
                .orElseThrow(LineNotFoundException::new);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("지하철노선 목록 조회한다")
    @Test
    void getLines() {
        List<Line> lines = Arrays.asList(new Line("2호선"), new Line("1호선"));
        lineRepository.saveAll(lines);
        entityManager.clear();

        List<Line> actual = lineRepository.findAll();

        assertThat(actual.size()).isEqualTo(2);
        assertThat(actual).containsExactlyElementsOf(lines);
    }

    @DisplayName("id가 존재한다면 지하철노선을 조회한다")
    @Test
    void getLineWithValidId() {
        Line line = new Line("2호선");
        lineRepository.save(line);
        entityManager.close();

        Line actual = lineRepository.findById(1L).orElseThrow(LineNotFoundException::new);

        assertThat(actual).isEqualTo(line);
    }

    @DisplayName("id가 존재하지 않는다면 지하철노선을 조회한다")
    @Test
    void getLineWithInvalidId() {
        assertThatThrownBy(() -> lineRepository.findById(1L).orElseThrow(LineNotFoundException::new))
                .isInstanceOf(LineNotFoundException.class);
    }

    @AfterEach
    void tearDown() {
        lineRepository.deleteAll();
        this.entityManager
                .createNativeQuery("ALTER TABLE LINE ALTER COLUMN `id` RESTART WITH 1")
                .executeUpdate();
    }
}
