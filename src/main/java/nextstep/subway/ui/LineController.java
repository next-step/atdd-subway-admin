package nextstep.subway.ui;

import nextstep.subway.application.LineService;
import nextstep.subway.dto.LineCreateRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
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

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineCreateRequest request) {
        LineResponse lineResponse = lineService.saveLine(request);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = lineService.findAllLines();
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> showLinesById(@PathVariable Long id) {
        LineResponse lineResponses = lineService.findById(id);
        return ResponseEntity.ok().body(lineResponses);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<?> updateLine(@PathVariable Long id, @RequestBody LineUpdateRequest request) {
        lineService.updateLine(id, request);
        return ResponseEntity.ok().build();
    }
}
