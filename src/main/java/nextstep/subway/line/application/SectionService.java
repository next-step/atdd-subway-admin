package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.exception.StationNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionService {

  private static final String LINE_NOT_FOUND_MSG = "지하철 역을 찾을 수 없습니다.";

  private final StationRepository stationRepository;

  public SectionService(StationRepository stationRepository) {
    this.stationRepository = stationRepository;
  }

  public Section selectSection(Line line, Long upStationId, Long downStationId, int distance) {
    return new Section(
        line,
        stationRepository.findById(upStationId).orElseThrow(() -> new StationNotFoundException(LINE_NOT_FOUND_MSG)),
        stationRepository.findById(downStationId).orElseThrow(() -> new StationNotFoundException(LINE_NOT_FOUND_MSG)),
        distance
    );
  }

}
