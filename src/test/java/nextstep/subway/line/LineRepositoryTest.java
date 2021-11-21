package nextstep.subway.line;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import nextstep.subway.station.domain.StationRepository;

@DataJpaTest
public class LineRepositoryTest {

    @Autowired
    private StationRepository stationRepository;


    @Test
    void 지하철역_다건_조회() {
        List<Long> stationIds = Arrays.asList(2L,1L);
        stationRepository.findAllByIdIsIn(stationIds);
    }
}
