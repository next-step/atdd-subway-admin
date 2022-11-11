package nextstep.subway.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Optional;
import javax.persistence.NoResultException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class LineRepositoryTest {

    @Autowired
    LineRepository lineRepository;

    Station 강남역 = Station.from("강남역");
    Station 양재역 = Station.from("양재역");

    @DisplayName("노선을 신규 등록할 수 있다.")
    @Test
    void create() {
        Line line = Line.of("신분당선", "red", Station.from("강남역"), Station.from("양재역"), 10);

        Line actual = lineRepository.save(line);

        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getDistance()).isEqualTo(line.getDistance()),
                () -> assertThat(actual.getColor()).isEqualTo(line.getColor()),
                () -> assertThat(actual.getUpStation()).isEqualTo(강남역),
                () -> assertThat(actual.getDownStation()).isEqualTo(양재역)
        );
    }

    @DisplayName("노선을 조회할 수 있다.")
    @Test
    void read() {
        Line actual = lineRepository.save(Line.of("신분당선", "red", Station.from("강남역"), Station.from("양재역"), 10));

        Optional<Line> expect = lineRepository.findById(actual.getId());

        assertThat(expect).isPresent();
    }

    @DisplayName("노선을 이름과 색을 변경할 수 있다.")
    @Test
    void update() {
        String newName = "다른분당선";
        String newColor = "blue";
        Line actual = lineRepository.save(Line.of("신분당선", "red", Station.from("강남역"), Station.from("양재역"), 10));

        actual.changeNameAndColor(newName, newColor);
        Optional<Line> expect = lineRepository.findById(actual.getId());

        assertAll(
                () -> assertThat(expect.orElseThrow(NoResultException::new).getName()).isEqualTo(newName),
                () -> assertThat(expect.orElseThrow(NoResultException::new).getColor()).isEqualTo(newColor)
        );
    }

    @DisplayName("노선을 삭제할 수 있다.")
    @Test
    void delete() {
        Line line = lineRepository.save(Line.of("신분당선", "red", Station.from("강남역"), Station.from("양재역"), 10));

        lineRepository.delete(line);
        Optional<Line> actual = lineRepository.findById(line.getId());

        assertThat(actual).isNotPresent();
    }
}
