package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesResponse;
import org.springframework.dao.DataIntegrityViolationException;
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
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        ResponseEntity<LineResponse> response;

        LineResponse line = lineService.saveLine(lineRequest);
        response = ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);

        return response;
    }

    @GetMapping
    public ResponseEntity<LinesResponse> getLines(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = "") String color) {
        LineRequest lineRequest = new LineRequest(name, color);
        ResponseEntity<LinesResponse> response = null;

        LinesResponse linesResponse = lineService.getLines(lineRequest);
        if (linesResponse.isEmpty()) {
            response = ResponseEntity.noContent().build();
        } else if (!linesResponse.isEmpty()) {
            response = ResponseEntity.ok().body(linesResponse);
        }

        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
        ResponseEntity<LineResponse> response = null;

        LineResponse linesResponse = lineService.getLineById(id);
        response = ResponseEntity.ok().body(linesResponse);

        return response;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        ResponseEntity<LineResponse> response = null;

        LineResponse linesResponse = lineService.updateLine(id, lineRequest);
        response = ResponseEntity.ok().body(linesResponse);

        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);

        return ResponseEntity.noContent().build();
    }
}
