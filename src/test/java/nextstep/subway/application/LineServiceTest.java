package nextstep.subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;
import javax.persistence.EntityNotFoundException;
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

    @Test
    void saveLine() {
        LineRequest expected = new LineRequest("신분당선", "bg-red-600", 10, "1", "2");
        LineResponse actual = service.saveLine(expected);
        assertThat(actual.getName()).isEqualTo(expected.getName());
    }

    @Test
    void findAllLines() {
        service.saveLine(new LineRequest("신분당선", "bg-red-600", 10, "1", "2"));
        List<LineResponse> allLines = service.findAllLines();
        assertThat(allLines).hasSize(1);
    }

    @Test
    void findByName() {
        LineResponse expected = service.saveLine(new LineRequest("신분당선", "bg-red-600", 10, "1", "2"));
        LineResponse actual = service.findByName("신분당선");
        assertThat(actual.getName()).isEqualTo(expected.getName());
    }

    @Test
    void updateLine() {
        service.saveLine(new LineRequest("신분당선", "bg-red-600", 10, "1", "2"));
        LineRequest request = new LineRequest("신분당선2", "bg-red-600", 10, "1", "2");
        service.updateLine("신분당선", request);
    }

    @Test
    void deleteLineById() {
        LineResponse saveLine = service.saveLine(new LineRequest("신분당선", "bg-red-600", 10, "1", "2"));
        service.deleteLineById(saveLine.getId());
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> service.findByName("신분당선"));
    }

}
