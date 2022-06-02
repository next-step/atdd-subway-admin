package nextstep.subway.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LineRepositoryTest {
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

    Line line;
    Station upStation;
    Station downStation;
    Section section;

    @BeforeEach
    void setUp() {
        upStation = Station.builder("지하철역")
                .build();
        downStation = Station.builder("새로운지하철역")
                .build();
        Station save1 = stationRepository.save(upStation);
        Station save2 = stationRepository.save(downStation);
        section = Section.builder(save1, save2, Distance.valueOf(10))
                .build();
        line = Line.builder("신분당선", "bg-red-600")
                .build();
        line.addSection(section);
    }

    @DisplayName("노선 저장 테스트")
    @Test
    void save() {
        Line save = lineRepository.save(line);
        assertAll(
                () -> assertThat(save.id()).isNotNull(),
                () -> assertThat(save.color()).isEqualTo("bg-red-600"),
                () -> assertThat(save.name()).isEqualTo("신분당선")
        );
    }

    @DisplayName("노선 ID로 조회하는 테스트")
    @Test
    void findByName() {
        Line savedLine = lineRepository.save(line);
        Line line = lineRepository.findById(savedLine.id()).get();
        assertAll(
                () -> assertThat(line.id()).isNotNull(),
                () -> assertThat(line.color()).isEqualTo("bg-red-600"),
                () -> assertThat(line.name()).isEqualTo("신분당선")
        );
    }
}
