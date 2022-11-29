package nextstep.subway;

import nextstep.subway.application.LineService;
import nextstep.subway.application.SectionService;
import nextstep.subway.application.StationService;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.StationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {
    protected static final String DELIMITER = "/";

    @LocalServerPort
    protected int port;

    @Autowired
    protected DatabaseCleaner databaseCleaner;

    @Autowired
    StationService stationService;

    @Autowired
    LineService lineService;

    @Autowired
    SectionService sectionService;

    @Transactional
    public long creatStation(String name) {
        return stationService.saveStation(new StationRequest(name)).getId();
    }

    @Transactional
    public void createLine(String name, String color, long upStationId, long downStationId, long distance) {
        sectionService.saveStation(new Section(stationService.findStation(upStationId), stationService.findStation(downStationId), distance));
        lineService.saveLine(new LineRequest(name, color, upStationId, downStationId, distance));
    }

    public Line findLine(String name) {
        return lineService.findLine(name);
    }

    public Station findStation(String name) {
        return stationService.findStation(name);
    }
}
