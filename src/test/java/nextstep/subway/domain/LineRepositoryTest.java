package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class LineRepositoryTest {

    @Autowired
    private LineRepository lineRepository;

    @Test
    @DisplayName("노선 저장")
    void save() {
        // given
        String name = "8호선";
        String color = "분홍색";
        Station upStation = new Station("잠실역");
        Station downStation = new Station("장지역");
        Line line = Line.of(name, color, upStation, downStation);

        // expect
        assertThat(lineRepository.save(line)).isNotNull();
    }
}
