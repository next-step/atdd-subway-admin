package nextstep.subway.line;

import nextstep.subway.domain.*;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class EntityTest {
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
        this.line = new Line("신분당선", "bg-red-600", S1, S2, new Distance(10L));
    }

    @DisplayName("동등성 비교 테스트")
    @Test
    void identityTest() {
        Line savedLine = lineRepository.save(line);
        AssertionsForClassTypes.assertThat(lineRepository.findById(savedLine.getId()).orElseThrow(EntityNotFoundException::new)).isEqualTo(savedLine);
    }

    @DisplayName("Station 과 연관 관계 테스트")
    @Test
    void relatedTest() {
        Line savedLine = lineRepository.save(line);
        assertThat(stationRepository.findById(savedLine.getUpStation().getId()).orElseThrow(EntityNotFoundException::new)).isEqualTo(S1);
        assertThat(stationRepository.findById(savedLine.getDownStation().getId()).orElseThrow(EntityNotFoundException::new)).isEqualTo(S2);
    }
}
