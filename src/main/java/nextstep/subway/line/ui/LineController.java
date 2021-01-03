package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.*;
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
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        LineCreateResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }

    @GetMapping(path = "/{lineId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long lineId) {
        return ResponseEntity.ok().body(lineService.findById(lineId));
    }

    @PutMapping(path = "/{lineId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
        lineService.updateLine(lineId, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/{lineId}")
    public ResponseEntity deleteLine(@PathVariable Long lineId) {
        lineService.deleteLineById(lineId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity addSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        // TODO: 구간 등록 기능 구현
        // ...

        SectionResponse sectionResponse = new SectionResponse();
        Long sectionId = 1L;
        return ResponseEntity.created(URI.create("/lines/" + lineId + "/sections/" + sectionId)).body(sectionResponse);
    }
}
