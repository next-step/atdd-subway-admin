package nextstep.subway.domain.line.ui;

import nextstep.subway.domain.line.application.LineService;
import nextstep.subway.domain.line.dto.LineRequest;
import nextstep.subway.domain.line.dto.LineResponse;
import nextstep.subway.domain.line.dto.SectionRequest;
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
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> findAllLines() {
        return ResponseEntity.ok().body(lineService.findAllLine());
    }

    @PutMapping("/{lineId}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable("lineId") Long lineId,
                                                   @RequestBody LineRequest lineRequest) {
        return ResponseEntity.ok().body(lineService.modifyLine(lineId, lineRequest));
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity<Void> deleteLine(@PathVariable("lineId") Long lineId) {
        lineService.deleteLine(lineId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{lineNo}")
    public ResponseEntity<LineResponse> findLine(@PathVariable("lineNo") Long lineNo) {
        return ResponseEntity.ok(lineService.findDetailLine(lineNo));
    }

    @PostMapping("/{lineNo}/sections")
    public ResponseEntity<Void> createSection(@PathVariable("lineNo") Long lineNo,
                                                      @RequestBody SectionRequest sectionRequest) {
        lineService.saveSection(lineNo, sectionRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{lineNo}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable("lineNo") Long lineNo,
                                              @RequestParam("stationId") Long stationId) {
        lineService.removeSection(lineNo, stationId);
        return ResponseEntity.ok().build();
    }
}
