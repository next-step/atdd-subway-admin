package nextstep.subway.application;

import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@Transactional
public class LineServiceTest {

    public static LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10L);
    @Autowired
    private LineRepository lineRepository;

    private LineService lineService;

    @Autowired
    private StationRepository stationRepository;

    private  Station savedStation1;
    private  Station savedStation2;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository, stationRepository);
        savedStation1 = stationRepository.save(new Station("새로운지하철"));
        savedStation2 = stationRepository.save(new Station("새로운지하철1"));
    }

    @DisplayName("생성시 LineRepository 가 필요하다")
    @Test
    void createTest() {
        assertThat(lineService).isEqualTo(new LineService(lineRepository, stationRepository));
    }

    @DisplayName("LineRequest 를 입력받아서 저장한다.")
    @Test
    void saveTest() {
        LineResponse lineResponse = lineService.saveLine(new LineRequest("신분당선", "bg-red-600", savedStation1.getId(), savedStation2.getId(), 10L));
        assertThat(lineResponse.isContainsBy(savedStation1)).isTrue();
        assertThat(lineResponse.isContainsBy(savedStation2)).isTrue();
    }

    @DisplayName("Line 정보를 조회한다.")
    @Test
    void findAllTest() {
        LineResponse lineResponse = lineService.saveLine(new LineRequest("신분당선", "bg-red-600", savedStation1.getId(), savedStation2.getId(), 10L));
       assertThat(lineService.findAllLine()).contains(lineResponse);
    }

    @DisplayName("특정 Line 정보를 조회한다")
    @Test
    void findTest() {
        LineResponse lineResponse = lineService.saveLine(new LineRequest("신분당선", "bg-red-600", savedStation1.getId(), savedStation2.getId(), 10L));
        assertThat(lineService.findLine(lineResponse.getId())).isEqualTo(lineResponse);
    }

    @DisplayName("변경할 id 에 LineRequest 정보를 업데이트 한다.")
    @Test
    void updateTest() {
        LineResponse lineResponse = lineService.saveLine(new LineRequest("신분당선", "bg-red-600", savedStation1.getId(), savedStation2.getId(), 10L));
        LineRequest request = new LineRequest("test", "bg-red-600", null, null, null);
        lineService.updateLine(lineResponse.getId(), request);
        assertThat(lineRepository.findById(lineResponse.getId()).get().getName()).isEqualTo("test");
    }
}