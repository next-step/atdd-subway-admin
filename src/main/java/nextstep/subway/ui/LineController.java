package nextstep.subway.ui;

import nextstep.subway.application.LineService;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest request) {
        LineResponse response = lineService.createLine(request);
        return ResponseEntity.created(URI.create("/lines/" + response.getId())).body(response);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> getLines() {
        return ResponseEntity.ok(lineService.toLineResponses());
    }

    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
        LineResponse response = lineService.toLineResponse(id);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest request) {
        lineService.updateLine(id, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<Void> addSection(@PathVariable Long id, @RequestBody SectionRequest request) {
        lineService.addSection(id, request);
        return ResponseEntity.ok().build();
    }
}
