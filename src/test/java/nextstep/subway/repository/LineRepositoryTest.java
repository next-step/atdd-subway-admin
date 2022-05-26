package nextstep.subway.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.subway.domain.Line;
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

    @BeforeEach
    void setUp() {
        upStation = Station.builder("지하철역")
                .build();
        downStation = Station.builder("새로운지하철역")
                .build();
        Station save1 = stationRepository.save(upStation);
        Station save2 = stationRepository.save(downStation);
        line = Line.builder("신분당선", "bg-red-600", 10)
                .build()
                .addUpStation(save1)
                .addDownStation(save2);
    }

    @DisplayName("노선 저장 테스트")
    @Test
    void save() {
        Line save = lineRepository.save(line);
        assertAll(
                () -> assertThat(save.getId()).isNotNull(),
                () -> assertThat(save.getColor()).isEqualTo("bg-red-600"),
                () -> assertThat(save.getName()).isEqualTo("신분당선"),
                () -> assertThat(save.getUpStation().getName()).isEqualTo(upStation.getName())
        );
    }
}
