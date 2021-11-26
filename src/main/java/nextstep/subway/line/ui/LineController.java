package nextstep.subway.line.ui;

import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

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
    public ResponseEntity getLines() {
        LinesResponse lines = lineService.getLines();
        return ResponseEntity.ok(lines.getLines());
    }

    @GetMapping("/{id}")
    public ResponseEntity getLine(@PathVariable("id") Long id) {
        try {
            LineResponse line = lineService.getLine(id);
            return ResponseEntity.ok(line);
        } catch (NotFoundException e) {
            return ResponseEntity.noContent().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity updateLine(@PathVariable("id") Long id, @RequestBody LineRequest lineRequest) {
        try {
            LineResponse line = lineService.updateLine(id, lineRequest);
            return ResponseEntity.ok(line);
        } catch (NotFoundException e) {
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable("id") Long id) {
        lineService.deleteLine(id);
        return ResponseEntity.ok().build();
    }
}