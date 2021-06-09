package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

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
    public ResponseEntity<List<LineResponse>> findAllLines() {
        return ResponseEntity.ok(lineService.findLines());
    }

    @GetMapping("/{lineId}")
    public ResponseEntity<LineResponse> findLine(@PathVariable long lineId) {
        return ResponseEntity.ok(lineService.findLine(lineId));
    }

    @PutMapping("/{lineId}")
    public ResponseEntity updateLine(@PathVariable long lineId, @RequestBody Line lineRequest) {
        lineService.updateLine(lineId, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity deleteLine(@PathVariable long lineId) {
        lineService.deleteLine(lineId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity addSection(@PathVariable long lineId, @RequestBody SectionRequest sectionRequest) {
        LineResponse lineResponse = lineService.addSection(lineId, sectionRequest);

        return ResponseEntity.ok().body(lineResponse);
    }

    @ExceptionHandler({DataIntegrityViolationException.class,
            NoSuchElementException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity handleIllegalArgsException() {
        return ResponseEntity.badRequest().build();
    }
}
