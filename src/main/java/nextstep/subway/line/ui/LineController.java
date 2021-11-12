package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.LineNameDuplicatedException;
import nextstep.subway.line.domain.LineNotFoundException;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    @GetMapping
    public ResponseEntity<List<LineResponse>> getLines() {
        return ResponseEntity.ok(lineService.getLines());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
        return ResponseEntity.ok(lineService.getLine(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest updateLineRequest) {
        lineService.updateLine(id, updateLineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(LineNameDuplicatedException.class)
    public void handleLineNameDuplicatedException(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @ExceptionHandler(LineNotFoundException.class)
    public void handleEntityNotFoundException(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}
