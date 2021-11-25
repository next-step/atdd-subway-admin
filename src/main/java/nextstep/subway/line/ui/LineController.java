package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.dto.SectionResponse;
import nextstep.subway.line.exception.NotFoundLineException;
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
        return ResponseEntity.ok().body(lineService.getLines());
    }

    @GetMapping("/{id}")
    public ResponseEntity getLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(lineService.getLine(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.update(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity createSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        SectionResponse sectionResponse = lineService.saveSection(id, sectionRequest);
        return ResponseEntity.created(URI.create("/lines/" + id + "/sections/" + sectionResponse.getId())).body(sectionResponse);
    }

}
