package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
public class LineRepositoryTest {

    private static final String 라인_색 = "bg-green-600";
    private static final String 라인_이름 = "2호선";
    private static final int 거리 = 3;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void save() {
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));

        Line 이호선 = lineRepository.save(new Line(라인_이름, 라인_색, 강남역, 역삼역, 거리));
        assertAll(
                () -> assertThat(이호선.getColor()).isEqualTo(라인_색),
                () -> assertThat(이호선.getName()).isEqualTo(라인_이름),
                () -> assertThat(이호선.getSections().size()).isEqualTo(1)
        );

        entityManager.flush();
        entityManager.clear();

        Optional<Line> line = lineRepository.findById(이호선.getId());
        assertAll(
                () -> assertThat(line.isPresent()).isTrue(),
                () -> assertThat(line.get().getColor()).isEqualTo(라인_색),
                () -> assertThat(line.get().getName()).isEqualTo(라인_이름),
                () -> assertThat(line.get().getSections().size()).isEqualTo(1)
        );
    }
}
