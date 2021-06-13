package nextstep.subway.line.domain;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("LineRepository 관련 테스트")
public class LineRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private StationRepository stationRepository;

    private Station upStation;
    private Station downStation;
    private int distance;
    private Section section;
    private Line line2;
    private Line line6;

    @BeforeEach
    void setup() {
        upStation = stationRepository.save(Station.of("강남역"));
        downStation = stationRepository.save(Station.of("역삼역"));
        distance = 5;

        section = sectionRepository.save(Section.of(upStation, downStation, distance));
    }

    @Test
    void create() {
        Line persistLine = lineRepository.save(Line.of("2호선", "다크그린", section));

        assertThat(persistLine.stations()).contains(upStation, downStation);
    }

    @Test
    void findById() {
        line2 = lineRepository.save(Line.of("2호선", "다크그린", section));

        testEntityManager.flush();
        testEntityManager.clear();

        Line persistLine = lineRepository.findById(line2.getId()).orElseThrow(NoSuchElementException::new);

        List<String> names = persistLine.stations().stream()
                .map(station -> station.getName())
                .collect(Collectors.toList());
        assertThat(names).contains("강남역", "역삼역");
    }

    @Test
    void update() {
        line2 = lineRepository.save(Line.of("2호선", "다크그린", section));

        Line updateLine = Line.of("name", "color");

        line2.update(updateLine);

        testEntityManager.flush();
        testEntityManager.clear();

        Line actualLine = lineRepository.findById(line2.getId()).orElseThrow(NoSuchElementException::new);

        assertThat(updateLine.getName()).isEqualTo(actualLine.getName());
        assertThat(updateLine.getColor()).isEqualTo(actualLine.getColor());
    }

}
