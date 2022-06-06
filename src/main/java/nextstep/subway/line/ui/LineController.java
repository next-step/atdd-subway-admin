package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import org.springframework.dao.DataIntegrityViolationException;
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

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(lineService.findLineById(id));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }

    @PatchMapping("/{id}")
    public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        return ResponseEntity.ok().body(lineService.updateLineById(id, lineRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/{stationId}/sections")
    public ResponseEntity removeLineStation(@PathVariable Long id, Long stationId) {
        lineService.removeSection(id, stationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity addSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        return ResponseEntity.ok().body(lineService.addSection(id, sectionRequest));
    }

    //안해주면 인수 테스트에서 BadRequest 대신 400번 Error 가 발생할 수 있다.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handleIllegalArgsException(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().build();
    }
}
