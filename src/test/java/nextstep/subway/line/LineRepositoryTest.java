package nextstep.subway.line;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class LineRepositoryTest {
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    TestEntityManager entityManager;

    @DisplayName("노선 저장")
    @Test
    public void save() throws Exception {
        //given
        Line line = Line.create("신분당선", "red");

        //when
        Line savedLine = lineRepository.save(line);
        entityManager.flush();
        entityManager.clear();

        //then
        Line findLine = lineRepository.findById(savedLine.getId()).orElseThrow(() -> new IllegalStateException());
        assertThat(findLine).isEqualTo(savedLine);
    }

    @DisplayName("노선 이름 중복 확인: 중복인 경우")
    @Test
    public void existsByName() throws Exception {
        //given
        노선저장("testName", "testColor");

        //when
        boolean isExits = lineRepository.existsByName("testName");

        //then
        assertThat(isExits).isTrue();
    }

    @DisplayName("노선 이름 중복 확인: 중복이 아닌 경우")
    @Test
    public void existsByName2() throws Exception {
        //given
        노선저장("testName", "testColor");

        //when
        boolean isExits = lineRepository.existsByName("testName123");

        //then
        assertThat(isExits).isFalse();
    }

    Line 노선저장(String name, String color) {
        Line line = Line.create(name, color);

        Line savedLine = lineRepository.save(line);
        entityManager.flush();
        entityManager.clear();

        return savedLine;
    }
}
