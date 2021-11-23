package nextstep.subway.station.application;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import nextstep.subway.station.exception.StationNotFoundException;

@SpringBootTest
class StationServiceTest {

    @Autowired
    private StationService stationService;

    @Test
    void 존재하지_않는_지하철역_조회시_예외() {
        assertThatThrownBy(() -> {
            stationService.findByIdThrow(1L);
        }).isInstanceOf(StationNotFoundException.class);
    }
}