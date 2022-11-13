package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import nextstep.subway.application.LineService;
import nextstep.subway.dto.LineEditRequest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public class LineServiceTest {

    @Autowired
    LineService lineService;

    @Test
    @DisplayName("지하철 노선을 생성한다.")
    void save(){
        // given
        LineRequest lineRequest = new LineRequest("2호선", "bg-color-060", 1L, 2L, 10);

        //when
        LineResponse lineResponse = lineService.saveLine(lineRequest);

        //then
        assertThat(lineResponse.getName()).isEqualTo("2호선");
    }

    @Test
    @DisplayName("지하철 노선을 조회한다.")
    void findId(){
        // given
        LineResponse saveLine = lineService.saveLine(new LineRequest("2호선", "bg-color-060", 1L, 2L, 10));

        // when
        LineResponse findLine = lineService.findLine(saveLine.getId());

        // then
        assertThat(saveLine).isEqualTo(findLine);
    }

    @Test
    @DisplayName("지하철 노선 목록을 조회한다.")
    void findAll(){
        // given
        LineResponse lineResponse1 = lineService.saveLine(new LineRequest("2호선", "bg-color-060", 1L, 2L, 10));
        LineResponse lineResponse2 = lineService.saveLine(new LineRequest("8호선", "bg-color-330", 1L, 2L, 10));

        // when
        List<LineResponse> lines = lineService.findAllLines();

        // then
        assertThat(lines).containsExactly(
                lineResponse1, lineResponse2
        );
    }

    @Test
    @DisplayName("지하철 노선을 삭제한다.")
    void delete(){
        // given
        LineResponse lineResponse = lineService.saveLine(new LineRequest("2호선", "bg-color-060", 1L, 2L, 10));

        // when
        lineService.deleteLine(lineResponse.getId());

        // then
        List<LineResponse> lines = lineService.findAllLines();
        assertThat(lines).doesNotContain(lineResponse);
    }

    @Test
    @DisplayName("지하철 노선 정보를 수정한다.")
    void update(){
        // given
        LineResponse saveLine = lineService.saveLine(new LineRequest("2호선", "bg-color-060", 1L, 2L, 10));

        // when
        lineService.updateLine(saveLine.getId(), new LineEditRequest("3호선", "bg-color-006"));

        // then
        LineResponse findLine = lineService.findLine(saveLine.getId());
        assertThat(findLine.getName()).isEqualTo("3호선");
        assertThat(findLine.getColor()).isEqualTo("bg-color-006");
    }
}
