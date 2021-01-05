package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {
    @Mock
    private LineRepository lineRepository;
    private LineService lineService;

    @BeforeEach
    void setUp() {
        lineService = new LineService((lineRepository));
    }

    @Test
    void save() {
        // given
        LineRequest lineRequest = new LineRequest("2호선", "green");

        when(lineRepository.save(any())).thenReturn(new Line("5호선", "purple"));

        // when
        LineResponse lineResponse = lineService.saveLine(lineRequest);

        // then
        assertThat(lineResponse).isNotNull();
        assertThat(lineResponse.getName()).isEqualTo("5호선");
    }

    @Test
    void findAll() {
        // given
        when(lineRepository.findAll()).thenReturn(Arrays.asList(new Line("2호선", "green"), new Line("5호선", "purple")));

        // when
        List<LineResponse> lineResponses = lineService.findAllLines();

        // then
        assertThat(lineResponses.size()).isEqualTo(2);
    }

    @Test
    void findById() {
        // given
        when(lineRepository.findById(any())).thenReturn(Optional.of(new Line("2호선", "green")));
        // when
        LineResponse lineResponse = lineService.findById(1L);
        // then
        assertThat(lineResponse.getName()).isEqualTo("2호선");
    }

    @DisplayName("노선이 없는 경우 조회하면 예외 발생")
    @Test
    void findByIdThrow() {
        assertThatThrownBy(()->{
            lineService.findById(1L);
        }).isInstanceOf(EntityNotFoundException.class);
    }
}