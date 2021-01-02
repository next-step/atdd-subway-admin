package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.application.StationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionService {

  private final StationService stationService;

  public SectionService(StationService stationService) {
    this.stationService = stationService;
  }

  public Section createSection(Line line, Long upStationId, Long downStationId, int distance) {
    return new Section(
        line,
        stationService.findById(upStationId),
        stationService.findById(downStationId),
        distance
    );
  }

}
