package nextstep.subway.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineStationResponse;
import nextstep.subway.dto.SectionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class LineStationServiceTest {


    @Autowired
    LineService lineService;

    @Autowired
    LineStationService lineStationService;

    @Autowired
    StationRepository stationRepository;

    @Autowired
    EntityManager em;

    Station station1 = null;
    Station station2 = null;
    Station station3 = null;

    @Test
    void addSection() {
        Long lineId = lineService.saveLine(getLineRequest());
        lineStationService.addSection(lineId, getSectionRequest());
        flushAndClear();
        List<LineStationResponse> lineStations = lineStationService.findLineStationsByLineId(lineId);
        List<Integer> distances = lineStations.stream().map(LineStationResponse::getDistance)
                .collect(Collectors.toList());
        assertThat(distances).contains(6, 4);
    }

    @Test
    void findLineStationsByLineId() {
        Long lineId = lineService.saveLine(getLineRequest());
        List<LineStationResponse> lineStation = lineStationService.findLineStationsByLineId(lineId);
        assertThat(lineStation).hasSize(1);
    }

    private LineRequest getLineRequest() {
        station1 = stationRepository.save(new Station("경기 광주역"));
        station2 = stationRepository.save(new Station("중앙역"));
        station3 = stationRepository.save(new Station("모란역"));
        return new LineRequest("신분당선", "bg-red-600", 10, station1.getId(), station2.getId());
    }

    private SectionRequest getSectionRequest() {
        return new SectionRequest(station1.getId(), station3.getId(), 4);
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}
