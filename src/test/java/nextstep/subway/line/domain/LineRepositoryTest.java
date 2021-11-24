package nextstep.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LineRepositoryTest {

    @Autowired
    private LineRepository lineRepository;

    @Test
    @DisplayName("노선 이름으로 노선을 조회한다.")
    void findByName() {
        // given
        Line saveLine = lineRepository.save(Line.of("4호선", "bg-skyblue-600"));

        // when
        Line result = lineRepository.findByName(saveLine.getName()).get();

        // then
        assertThat(result).isEqualTo(saveLine);
    }
}