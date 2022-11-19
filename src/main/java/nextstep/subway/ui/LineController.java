package nextstep.subway.ui;

import nextstep.subway.application.LineService;
import nextstep.subway.dto.request.LineRequest;
import nextstep.subway.dto.response.LineReponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {
    private LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping(value = "/lines")
    public ResponseEntity<LineReponse> createLine(@RequestBody LineRequest lineRequest) {
        LineReponse line = lineService.createLine(lineRequest);
        return ResponseEntity.created(URI.create( "/lines/" +  line.getId()) ).body(line);
    }

    @GetMapping("/lines")
    public ResponseEntity getLines() {
        List<LineReponse> lines = lineService.getLines();
        return ResponseEntity.ok(lines);
    }

    @GetMapping("/line/{id}")
    public ResponseEntity getLine(@PathVariable Long id) {
        LineReponse line = lineService.getLine(id);
        return ResponseEntity.ok(line);
    }

    @PutMapping("/line/{id}")
    public ResponseEntity updateLine(@PathVariable Long id, @RequestBody  LineRequest lineRequest) {
        lineService.updateLine(id, lineRequest);
        LineReponse line = lineService.getLine(id);
        return ResponseEntity.ok(line);
    }

    @DeleteMapping("/line/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handleIllegalArgsException() {
        return ResponseEntity.badRequest().build();
    }
}
