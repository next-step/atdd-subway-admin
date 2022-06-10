package nextstep.subway.line;

import nextstep.subway.domain.*;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityNotFoundException;

@DataJpaTest
public class LineEntityTest {
    @Autowired
    LineRepository lineRepository;

    @Autowired
    StationRepository stationRepository;

    Line line;
    Station S1;
    Station S2;

    @BeforeEach
    void setUp() {
        S1 = stationRepository.save(new Station("지하철역"));
        S2 = stationRepository.save(new Station("새로운지하철역"));
        this.line = new Line("신분당선", "bg-red-600");
    }

    @DisplayName("동등성 비교 테스트")
    @Test
    void identityTest() {
        Line savedLine = lineRepository.save(line);
        AssertionsForClassTypes.assertThat(lineRepository.findById(savedLine.getId()).orElseThrow(EntityNotFoundException::new)).isEqualTo(savedLine);
    }
}
