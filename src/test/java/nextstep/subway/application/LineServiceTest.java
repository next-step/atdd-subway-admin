package nextstep.subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.List;
import javax.persistence.EntityNotFoundException;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class LineServiceTest {

    @Autowired
    LineService service;

    @Autowired
    LineRepository lineRepository;

    @Autowired
    StationRepository stationRepository;

    Station station1 = null;
    Station station2 = null;

    @Test
    void saveLine() {
        LineRequest expected = getLineRequest();
        Long id = service.saveLine(expected);
        LineResponse actual = service.findById(id);
        assertThat(actual.getName()).isEqualTo(expected.getName());
    }

    @Test
    void findAllLines() {
        service.saveLine(getLineRequest());
        List<LineResponse> allLines = service.findAllLines();
        assertThat(allLines).hasSize(1);
    }

    @Test
    void findByName() {
        service.saveLine(getLineRequest());
        assertThatNoException().isThrownBy(() -> service.findByName("신분당선"));
    }

    @Test
    void findById() {
        Long id = service.saveLine(getLineRequest());
        assertThatNoException().isThrownBy(() -> service.findById(id));
    }

    @Test
    void updateLine() {
        Long id = service.saveLine(getLineRequest());
        LineRequest request = new LineRequest("신분당선2", "bg-green-600", 10, station1.getId(), station2.getId());
        service.updateLine("신분당선", request);
        LineResponse findLine = service.findById(id);
        assertThat(findLine.getName()).isEqualTo("신분당선2");
        assertThat(findLine.getColor()).isEqualTo("bg-green-600");
    }

    @Test
    void deleteLineById() {
        Long id = service.saveLine(getLineRequest());
        service.deleteLineById(id);
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> service.findByName("신분당선"));
    }

    private LineRequest getLineRequest() {
        station1 = stationRepository.save(new Station("경기 광주역"));
        station2 = stationRepository.save(new Station("중앙역"));
        return new LineRequest("신분당선", "bg-red-600", 10, station1.getId(), station2.getId());
    }
}
