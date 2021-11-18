package nextstep.subway.line.ui;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody final LineRequest lineRequest) {
        final LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> findAllLines() {
        final List<LineResponse> allLines = lineService.getAllLines();
        return ResponseEntity.ok(allLines);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> findLine(@PathVariable final Long id) {
        final LineResponse line = lineService.getLine(id);
        return ResponseEntity.ok(line);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@RequestBody final LineRequest lineRequest, @PathVariable final Long id) {
        lineService.updateLine(lineRequest, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable final Long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }
}
