package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @Mock
    private LineRepository lineRepository;

    @InjectMocks
    private LineService lineService;

    private Line line;

    @BeforeEach
    public void setUp() {
        line = new Line("1호선", "blue");
    }

    @Test
    @DisplayName("라인 한건 조회")
    public void findById() {
        Mockito.when(lineRepository.findById(ArgumentMatchers.anyLong()))
            .thenReturn(Optional.of(line));

        LineResponse byId = lineService.findLine(1L);

        assertAll(() -> {
            assertThat(byId.getName()).isEqualTo(line.getName());
            assertThat(byId.getColor()).isEqualTo(line.getColor());
        });
    }

    @Test
    @DisplayName("라인 한건 조회시 없을 경우 NotFoundException")
    public void findByIdNotFoundException() {
        Mockito.when(lineRepository.findById(ArgumentMatchers.anyLong()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> lineService.findLine(1L))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("라인 목록 조회")
    public void findAll() {
        Mockito.when(lineRepository.findAll())
            .thenReturn(Arrays.asList(line));
        List<LineResponse> lineResponseList = lineService.findLines();
        assertThat(lineResponseList.size()).isEqualTo(1);

        System.out.println(lineResponseList);
    }

}