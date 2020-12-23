package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
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
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public List<LineResponse> findAllLines() {
        return lineService.findByAll();
    }

    @GetMapping(path = "/{id}")
    public LineResponse findLine(@PathVariable Long id) {
        return lineService.findById(id);
    }

    @PutMapping(path = "/{id}")
    public LineResponse modifyLine(@RequestBody LineRequest lineRequest, @PathVariable Long id) {
        return lineService.updateLine(lineRequest, id);
    }


    @DeleteMapping(path = "/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        return ResponseEntity.ok(URI.create("/lines/" + id));
    }
}
