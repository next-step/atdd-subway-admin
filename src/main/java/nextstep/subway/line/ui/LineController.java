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

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable long id) {
        return ResponseEntity.ok().body(lineService.findById(id));
    }

    @PutMapping(value = "{id}")
    public ResponseEntity<LineResponse> updateLine(@RequestBody LineRequest lineRequest, @PathVariable long id) {
        return ResponseEntity.ok().body(lineService.updateLine(lineRequest, id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        lineService.deleteStationById(id);
        return ResponseEntity.noContent().build();
    }
}
