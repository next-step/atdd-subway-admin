package nextstep.subway.ui;

import nextstep.subway.application.LineService;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("line")
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse lineCreationResponse = lineService.createLine(lineRequest);
        return ResponseEntity.created(URI.create("/")).body(lineCreationResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> getLines() {
        List<LineResponse> lineListResponse = lineService.getLines();
        return ResponseEntity.ok(lineListResponse);
    }

    @PutMapping("{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        LineResponse lineUpdateResponse = lineService.updateLine(id, lineRequest);
        return ResponseEntity.ok(lineUpdateResponse);
    }

    @GetMapping("{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
        LineResponse lineResponse = lineService.getLine(id);
        return ResponseEntity.ok(lineResponse);
    }
}
