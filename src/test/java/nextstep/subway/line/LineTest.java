package nextstep.subway.line;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class LineTest {

    @Autowired
    private LineRepository lineRepository;

    private Line line;

    @BeforeEach
    void setUp() {
        this.line = new Line("신분당선", "bg-red-600", 1L, 2L, 10L);
    }
    @DisplayName("라인은 이름, 컬러, 상행역 id, 하행역 id, 거리 정보를 가진다.")
    @Test
    void createTest() {
        assertThat(line)
                .isEqualTo(new Line("신분당선", "bg-red-600", 1L, 2L, 10L));
    }

    @DisplayName("동등성 비교 테스트")
    @Test
    void identityTest() {
        Line savedLine = lineRepository.save(line);
        assertThat(lineRepository.findById(savedLine.getId()).get()).isEqualTo(savedLine);
    }


}
