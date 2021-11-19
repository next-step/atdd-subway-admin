package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
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

    @GetMapping
    public ResponseEntity<List<LineResponse>> getAllLines() {
        return ResponseEntity.ok().body(lineService.getAllLines());
    }

    @GetMapping(value = "{lineId}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long lineId) {
        return ResponseEntity.ok().body(lineService.getLine(lineId));
    }

    @PutMapping(value = "{lineId}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
        return ResponseEntity.ok().body(lineService.updateLine(lineId, lineRequest.toLine()));
    }

    @DeleteMapping (value = "{lineId}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable Long lineId) {
        lineService.deleteLineById(lineId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity addLineStation(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        lineService.addSection(lineId, sectionRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping ("/{lineId}/sections")
    public ResponseEntity deleteLineStation(@PathVariable Long lineId,  @RequestParam(name = "stationId") Long stationId) {
        lineService.deleteSection(lineId, stationId);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleIllegalArgsException(IllegalArgumentException e) {
        System.out.println("e.getMessage() = " + e.getMessage());
        return ResponseEntity.badRequest().build();
    }

}
