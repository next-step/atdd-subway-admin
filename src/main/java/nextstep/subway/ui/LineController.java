package nextstep.subway.ui;

import java.net.URI;
import nextstep.subway.application.LineService;
import nextstep.subway.dto.UpdateLineRequest;
import nextstep.subway.dto.line.CreateLineRequest;
import nextstep.subway.dto.line.LineResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/lines")
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity createLine(@RequestBody CreateLineRequest createLineRequest) {
        LineResponse lineResponse = lineService.saveLine(createLineRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping
    public ResponseEntity getAllLines() {
        return ResponseEntity.ok(lineService.findAll());
    }

    @GetMapping(value = "{id}")
    public ResponseEntity getLineById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(lineService.findById(id));
    }

    @PutMapping(value = "{id}")
    public ResponseEntity updateLineById(
        @PathVariable(value = "id") Long id,
        @RequestBody UpdateLineRequest updateLineRequest
    ) {
        lineService.updateLine(id, updateLineRequest);
        return ResponseEntity.ok(null);
    }
}
