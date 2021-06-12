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
        try {
            LineResponse line = lineService.saveLine(lineRequest);
            response = ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
        } catch (DataIntegrityViolationException e) {
            response = ResponseEntity.badRequest().build();
            e.printStackTrace();
        }

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

        try {
            LineResponse linesResponse = lineService.getLineById(id);
            response = ResponseEntity.ok().body(linesResponse);
        } catch (NullPointerException e) {
            e.printStackTrace();
            response = ResponseEntity.notFound().build();
        }

        return response;
    }
}
