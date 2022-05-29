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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
public class LineServiceTest {

    public static LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10L);
    @Autowired
    private LineRepository lineRepository;

    private LineService lineService;

    @Autowired
    private StationRepository stationRepository;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository, stationRepository);
    }

    @DisplayName("생성시 LineRepository 가 필요하다")
    @Test
    void createTest() {
        assertThat(lineService).isEqualTo(new LineService(lineRepository, stationRepository));
    }

    @DisplayName("LineRequest 를 입력받아서 저장한다.")
    @Test
    void saveTest() {
        Station savedStation1 = stationRepository.save(new Station("새로운지하철"));
        Station savedStation2 = stationRepository.save(new Station("새로운지하철1"));
        LineResponse lineResponse = lineService.saveLine(new LineRequest("신분당선", "bg-red-600", savedStation1.getId(), savedStation2.getId(), 10L));
        assertThat(lineResponse.isContainsBy(savedStation1)).isTrue();
        assertThat(lineResponse.isContainsBy(savedStation2)).isTrue();
    }
}