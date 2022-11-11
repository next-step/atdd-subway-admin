package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineCommandService;
import nextstep.subway.line.application.LineQueryService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {
    private final LineCommandService lineCommandService;
    private final LineQueryService lineQueryService;

    public LineController(LineCommandService lineCommandService, LineQueryService lineQueryService) {
        this.lineCommandService = lineCommandService;
        this.lineQueryService = lineQueryService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineCommandService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineQueryService.findAllLines());
    }

    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok().body(lineQueryService.findLine(id));
    }

    @PutMapping(value = "/lines/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable(name = "id") Long id,
                                                   @RequestBody LineUpdateRequest lineRequest) {
        lineCommandService.updateLine(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable(name = "id") Long id) {
        lineCommandService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }
}
