package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.MediaType;
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

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> getLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(lineService.findById(id));
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        return ResponseEntity.ok().body(lineService.updateLineById(id, lineRequest));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }
}
