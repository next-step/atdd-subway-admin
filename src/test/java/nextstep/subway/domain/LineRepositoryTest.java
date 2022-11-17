package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
        Station upStation = Station.from("잠실역");
        Station downStation = Station.from("장지역");
        Integer distance = 10;
        Line line = Line.of(name, color, upStation, downStation, distance);

        // when
        Line persistLine = lineRepository.save(line);

        // then
        assertThat(persistLine).isNotNull();
    }
}