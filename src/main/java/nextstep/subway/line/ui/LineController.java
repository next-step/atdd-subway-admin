package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.HttpStatus;
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

    @GetMapping(value = "/{name}")
    public ResponseEntity getLine(@PathVariable  String name) {
        List<LineResponse> lines = lineService.getLine(name);

        return ResponseEntity.ok().body(lines);
    }

    @GetMapping()
    public ResponseEntity getLines() {
        List<LineResponse> lines = lineService.findAll();

        return ResponseEntity.ok().body(lines);
    }

    @PostMapping
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = null;
        try {
            line = lineService.saveLine(lineRequest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @PutMapping
    public ResponseEntity updateLine(@RequestBody LineRequest lineRequest) {
        List<LineResponse> lines = lineService.updateLine(lineRequest);

        return ResponseEntity.ok().body(lines);
    }
}
