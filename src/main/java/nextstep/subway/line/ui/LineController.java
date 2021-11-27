package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.application.dto.LineUpdateRequest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.ExistDuplicatedNameException;
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

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> findLines() {
        List<LineResponse> lineResponses = lineService.findLines();
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/{lineId}")
    public ResponseEntity<LineResponse> findLine(@PathVariable Long lineId) {
        LineResponse lineResponse = lineService.findLine(lineId);
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping("/{lineId}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
        LineResponse lineResponse = lineService.update(new LineUpdateRequest(lineId, lineRequest));
        return ResponseEntity.ok().body(lineResponse);
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity deleteLine(@PathVariable Long lineId) {
        lineService.delete(lineId);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(ExistDuplicatedNameException.class)
    public ResponseEntity<String> handleSqlException(ExistDuplicatedNameException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
