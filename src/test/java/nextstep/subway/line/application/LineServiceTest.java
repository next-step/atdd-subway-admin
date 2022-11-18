package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.application.LineService;
import nextstep.subway.application.StationService;
import nextstep.subway.dto.LineEditRequest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
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

    @Autowired
    StationService stationService;

    StationResponse station1;
    StationResponse station2;
    @BeforeEach
    void beforeEach(){
        station1 = stationService.saveStation(new StationRequest("강남역"));
        station2 = stationService.saveStation(new StationRequest("잠실역"));
    }

    @Test
    @DisplayName("지하철 노선을 생성한다.")
    void save(){
        // given
        LineRequest lineRequest = new LineRequest("2호선", "bg-color-060", station1.getId(), station2.getId(), 10);

        //when
        LineResponse lineResponse = lineService.saveLine(lineRequest);

        //then
        assertThat(lineResponse.getName()).isEqualTo("2호선");
    }

    @Test
    @DisplayName("지하철 노선 생성 시, 상행종점과 하행종점이 같은 경우 예외가 발생한다.")
    void saveExceptionBySection(){
        // given
        LineRequest lineRequest = new LineRequest("2호선", "bg-color-060", station1.getId(), station1.getId(), 10);

        //when

        //then
        assertThatIllegalArgumentException().isThrownBy(
                () -> lineService.saveLine(lineRequest)
        );
    }

    @Test
    @DisplayName("지하철 노선을 조회한다.")
    void findId(){
        // given
        LineResponse saveLine = lineService.saveLine(new LineRequest("2호선", "bg-color-060", station1.getId(), station2.getId(), 10));

        // when
        LineResponse findLine = lineService.findLine(saveLine.getId());

        // then
        assertThat(saveLine.getId()).isEqualTo(findLine.getId());
    }

    @Test
    @DisplayName("지하철 노선 목록을 조회한다.")
    void findAll(){
        // given
        LineResponse lineResponse1 = lineService.saveLine(new LineRequest("2호선", "bg-color-060", station1.getId(), station2.getId(), 10));
        LineResponse lineResponse2 = lineService.saveLine(new LineRequest("8호선", "bg-color-330", station1.getId(), station2.getId(), 10));

        // when
        List<Long> lineIds = lineService.findAllLines().stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        // then
        assertThat(lineIds).containsExactly(
                lineResponse1.getId(), lineResponse2.getId()
        );
    }

    @Test
    @DisplayName("지하철 노선을 삭제한다.")
    void delete(){
        // given
        LineResponse lineResponse = lineService.saveLine(new LineRequest("2호선", "bg-color-060", station1.getId(), station2.getId(), 10));

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
        LineResponse saveLine = lineService.saveLine(new LineRequest("2호선", "bg-color-060", station1.getId(), station2.getId(), 10));

        // when
        lineService.updateLine(saveLine.getId(), new LineEditRequest("3호선", "bg-color-006"));

        // then
        LineResponse findLine = lineService.findLine(saveLine.getId());
        assertThat(findLine.getName()).isEqualTo("3호선");
        assertThat(findLine.getColor()).isEqualTo("bg-color-006");
    }
}
