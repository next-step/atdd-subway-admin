package nextstep.subway.line.ui;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static nextstep.subway.PageController.URIMapping.LINE;

@RestController
@RequestMapping(LINE)
@RequiredArgsConstructor
public class LineController {
    private final LineService lineService;

    @PostMapping
    public ResponseEntity createLine(@Valid @RequestBody final LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create(LINE + "/" + line.getId())).body(line);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable final Long id) {
        return ResponseEntity.ok().body(lineService.findLine(id));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity updateLine(@PathVariable final Long id, @RequestBody final LineRequest lineRequest) {
        lineService.updateLine(id, lineRequest);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable final Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity addSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        LineResponse line = lineService.appendSection(id, sectionRequest);
        return ResponseEntity.created(URI.create(LINE + "/" + line.getId())).body(line);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handleIllegalArgsException(final DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().build();
    }
}
