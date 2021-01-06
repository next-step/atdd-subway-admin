package nextstep.subway.line.application;

import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class LineServiceTest extends AcceptanceTest {

    @Autowired
    private LineService lineService;

    @Test
    void save() {
        // given
        LineRequest lineRequest = new LineRequest("2호선", "green");

        // when
        LineResponse lineResponse = lineService.save(lineRequest);

        // then
        assertThat(lineResponse).isNotNull();
        assertThat(lineResponse.getName()).isEqualTo("2호선");
    }

    @Test
    void findAll() {
        // given
        lineService.save(new LineRequest("2호선", "green"));
        lineService.save(new LineRequest("5호선", "purple"));

        // when
        List<LineResponse> lineResponses = lineService.findAll();

        // then
        assertThat(lineResponses.size()).isEqualTo(2);
    }

    @Test
    void findById() {
        // given
        lineService.save(new LineRequest("2호선", "green"));
        // when
        LineResponse lineResponse = lineService.findById(1L);
        // then
        assertThat(lineResponse.getName()).isEqualTo("2호선");
    }

    @DisplayName("노선이 없는 경우 조회하면 예외 발생")
    @Test
    void findByIdThrow() {
        assertThatThrownBy(() -> {
            lineService.findById(1L);
        }).isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("노선이 없는 경우 수정하면 예외 발생")
    @Test
    void updateThrow() {
        assertThatThrownBy(() -> {
            lineService.update(1L, new LineRequest("2호선", "black"));
        }).isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("노선이 없는 경우 삭하면 예외 발생")
    @Test
    void deleteThrow() {
        assertThatThrownBy(() -> {
            lineService.delete(1L);
        }).isInstanceOf(EntityNotFoundException.class);
    }
}