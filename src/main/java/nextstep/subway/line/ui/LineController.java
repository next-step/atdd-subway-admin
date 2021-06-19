package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
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
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.id())).body(line);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> allLine() {
        return ResponseEntity.ok().body(lineService.findAll());
    }

    @GetMapping("/{name}")
    public ResponseEntity<LineResponse> findLine(@PathVariable String name){
        LineResponse line = lineService.findByName(name);
        return ResponseEntity.ok().body(line);
    }

    @PatchMapping
    public ResponseEntity<LineResponse> updateLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.updateByName(lineRequest);
        return ResponseEntity.ok().body(line);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity deleteLine(@PathVariable String name) {
        lineService.deleteByName(name);
        return ResponseEntity.noContent().build();
    }
}
