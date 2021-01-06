package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {

  private static final String LINE_NOT_FOUND_MSG = "지하철 노선을 찾을 수 없습니다.";

  private final LineRepository lineRepository;
  private final SectionService sectionService;
  private final StationService stationService;

  public LineService(LineRepository lineRepository, SectionService sectionService, StationService stationService) {
    this.lineRepository = lineRepository;
    this.sectionService = sectionService;
    this.stationService = stationService;
  }

  public LineResponse saveLine(LineRequest request) {
    Line line = request.toLine(stationService.findById(request.getUpStationId()), stationService.findById(request.getDownStationId()));
    Line persistLine = lineRepository.save(line);
    return LineResponse.of(persistLine);
  }

  @Transactional(readOnly = true)
  public List<LineResponse> selectLines() {
    List<Line> persistLines = lineRepository.findAll();
    return persistLines.stream()
        .map(LineResponse::of)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public LineResponse selectLine(Long id) {
    return LineResponse.of(findById(id));
  }

  public void updateLine(Long id, LineRequest lineRequest) {
    findById(id).update(lineRequest.toLine());
  }

  public void deleteLine(Long id) {
    lineRepository.deleteById(id);
  }

  private Line findById(Long id) {
    return lineRepository.findById(id)
        .orElseThrow(() -> new LineNotFoundException(LINE_NOT_FOUND_MSG));
  }

  public LineResponse addLine(Long id, SectionRequest request) {
    Line persistLine = findById(id);
    persistLine.addSection(sectionService.createSection(persistLine, request.getUpStationId(), request.getDownStationId(), request.getDistance()));
    return LineResponse.of(persistLine);
  }

  public LineResponse removeSectionByStationId(Long id, Long stationId) {
    Line persistLine = findById(id);
    Station removeStation = stationService.findById(stationId);
    persistLine.removeSection(removeStation);
    return LineResponse.of(persistLine);
  }
}
