package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import javax.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class LineTest {

    private static final Line SIN_BOUN_DANG_LINE = new Line("신분당선","bg-red-600");

    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private EntityManager entityManager;

    @DisplayName("노선 정보를 업데이트 한다.")
    @Test
    void update(){
        //given
        Line line = lineRepository.save(SIN_BOUN_DANG_LINE);

        //when
        line.update("분당선","bg-yellow-600");
        entityManager.clear();

        //then
        Line actual = lineRepository.findById(line.getId()).get();
        assertThat(actual.getName()).isEqualTo("분당선");
        assertThat(actual.getColor()).isEqualTo("bg-yellow-600");
    }
}
