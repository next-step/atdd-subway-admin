package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

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
    public ResponseEntity<LinesResponse> getLines() {
        LinesResponse lines = lineService.getLines();
        return ResponseEntity.ok(lines);
    }

    @GetMapping("/{lineId}")
    public ResponseEntity<LineResponse> getLine(@PathVariable("lineId") Long id) {
        LineResponse line = lineService.getLine(id);
        return ResponseEntity.ok(line);
    }

    @PutMapping("/{lineId}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable("lineId") Long id, @RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.updateLine(id, lineRequest);
        return ResponseEntity.ok(line);
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity<Boolean> deleteLine(@PathVariable("lineId") Long id) {
        Boolean result = lineService.deleteLine(id);
        return ResponseEntity.ok(result);
    }
}
