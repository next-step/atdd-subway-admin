package nextstep.subway.station;

import nextstep.subway.AcceptanceTest;
import nextstep.subway.application.StationService;
import nextstep.subway.dto.StationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class StationAcceptanceFixture extends AcceptanceTest {
    @Autowired
    protected StationService stationService;

    @Transactional
    public long creatStation(String name) {
        return stationService.saveStation(new StationRequest(name)).getId();
    }
}
