package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.exception.SectionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {
  private final LineService lineService;

  public LineController(final LineService lineService) {
    this.lineService = lineService;
  }

  @PostMapping
  public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
    LineResponse line = lineService.saveLine(lineRequest);
    return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
  }

  @GetMapping()
  public ResponseEntity<List<LineResponse>> showLines() {
    return ResponseEntity.ok(lineService.selectLines());
  }

  @GetMapping("/{id}")
  public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
    return ResponseEntity.ok(lineService.selectLine(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
    lineService.updateLine(id, lineRequest);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity removeLine(@PathVariable Long id) {
    lineService.deleteLine(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{id}/sections")
  public ResponseEntity addSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
    LineResponse response = lineService.addLine(id, sectionRequest);
    return ResponseEntity.ok().body(response);
  }

  @DeleteMapping("/{lineId}/sections")
  public ResponseEntity removeLineStation(@PathVariable Long lineId, @RequestParam Long stationId) {
    LineResponse response = lineService.removeSectionByStationId(lineId, stationId);
    return ResponseEntity.ok().body(response);
  }
}
