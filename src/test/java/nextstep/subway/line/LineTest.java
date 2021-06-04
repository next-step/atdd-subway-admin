package nextstep.subway.line;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
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
@DisplayName("Line 관련 테스트")
public class LineTest {

    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private StationRepository stationRepository;

    @Test
    void create() {
        Station upStation = stationRepository.save(Station.of("강남역"));
        Station downStation = stationRepository.save(Station.of("역삼역"));
        int distance = 5;

        Section section = sectionRepository.save(Section.of(upStation, downStation, distance));

        Line persistLine = lineRepository.save(Line.of("2호선", "다크그린", section));

        assertThat(persistLine.getStations()).contains(upStation, downStation);
    }

    @Test
    void findById() {
        Station upStation = stationRepository.save(Station.of("강남역"));
        Station downStation = stationRepository.save(Station.of("역삼역"));
        int distance = 5;

        Section section = sectionRepository.save(Section.of(upStation, downStation, distance));

        Line expected = lineRepository.save(Line.of("2호선", "다크그린", section));

        testEntityManager.flush();
        testEntityManager.clear();

        Line persistLine = lineRepository.findById(expected.getId()).orElseThrow(NoSuchElementException::new);

        List<String> names = persistLine.getStations().stream()
                .map(station -> station.getName())
                .collect(Collectors.toList());
        assertThat(names).contains("강남역", "역삼역");
    }
}
