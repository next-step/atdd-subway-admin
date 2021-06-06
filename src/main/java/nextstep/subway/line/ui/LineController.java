package nextstep.subway.line.ui;

import java.net.URI;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody final LineRequest lineRequest) {
        final LineResponse line = saveLine(lineRequest);

        return ResponseEntity.created(createUri(line))
            .body(line);
    }

    private LineResponse saveLine(final LineRequest lineRequest) {
        return lineService.saveLine(lineRequest);
    }

    private URI createUri(final LineResponse line) {
        return URI.create("/lines/" + line.getId());
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> listLines() {
        final List<LineResponse> lineResponses = findLines();

        return ResponseEntity.ok(lineResponses);
    }

    private List<LineResponse> findLines() {
        return lineService.findLines();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<LineResponse> handleIllegalArgsException(final DataIntegrityViolationException e) {
        return ResponseEntity.badRequest()
            .build();
    }
}
