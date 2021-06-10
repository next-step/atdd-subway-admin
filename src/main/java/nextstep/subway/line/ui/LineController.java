package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
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

    @GetMapping(path = {"/", ""})
    public ResponseEntity<List<LineResponse>> getLineList() {
        List<LineResponse> line = lineService.getList();
        return ResponseEntity.ok(line);
    }

    @GetMapping(path = {"/{id}", "/{id}/"})
    public ResponseEntity<LineResponse> getLine(@PathVariable(name = "id") Long id) {
        LineResponse line = lineService.getLine(id);
        return ResponseEntity.ok(line);
    }

    @PutMapping(path = {"/{id}", "/{id}/"})
    public ResponseEntity update(@PathVariable(name = "id") Long id, @RequestBody LineRequest request) {
        lineService.updateLine(id, request);
        return ResponseEntity.ok().build();
    }

}
