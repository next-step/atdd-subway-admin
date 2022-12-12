package nextstep.subway.ui;

import nextstep.subway.application.LineService;
import nextstep.subway.dto.request.LineRequest;
import nextstep.subway.dto.request.LineSectionRequest;
import nextstep.subway.dto.response.LineReponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/lines")
public class LineController {
    private LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineReponse> createLine(@RequestBody LineRequest lineRequest) {
        LineReponse line = lineService.createLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public ResponseEntity getLines() {
        List<LineReponse> lines = lineService.getLines();
        return ResponseEntity.ok(lines);
    }

    @GetMapping("/{id}")
    public ResponseEntity getLine(@PathVariable Long id) {
        LineReponse line = lineService.getLine(id);
        return ResponseEntity.ok(line);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.updateLine(id, lineRequest);
        LineReponse line = lineService.getLine(id);
        return ResponseEntity.ok(line);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<LineReponse> addLineSections(@PathVariable Long id, @RequestBody LineSectionRequest lineSectionRequest) {
        LineReponse line = lineService.addLineSections(id, lineSectionRequest);
        return ResponseEntity.ok(line);
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity<LineReponse> deleteLineSections(@PathVariable Long id, @RequestParam("stationId") Long stationId) {
        lineService.deleteLineSections(id, stationId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity handleIllegalArgsException() {
        return ResponseEntity.badRequest().build();
    }
}
