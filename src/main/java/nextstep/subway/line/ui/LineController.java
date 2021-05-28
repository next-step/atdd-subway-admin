package nextstep.subway.line.ui;

import java.util.List;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

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
    public ResponseEntity<List<LineResponse>> getLines() {
        List<LineResponse> lines = lineService.findLines();
        return ResponseEntity.ok(lines);
    }

    @GetMapping(value = "/{lineId}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long lineId) {
        LineResponse line = lineService.findLine(lineId);
        return ResponseEntity.ok(line);
    }

    @PutMapping(value = "/{lineId}")
    public ResponseEntity<Void> updateLine(@PathVariable Long lineId,
                                                   @RequestBody LineRequest lineRequest) {
        lineService.updateLine(lineId, lineRequest);
        return ResponseEntity.ok().build();
    }
}
