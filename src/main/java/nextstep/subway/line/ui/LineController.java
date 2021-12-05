package nextstep.subway.line.ui;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import nextstep.subway.exception.DefaultException;
import nextstep.subway.exception.dto.ErrorMessage;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.exception.LineDuplicateException;
import nextstep.subway.line.exception.NotFoundLineException;
import nextstep.subway.station.exception.NotFoundStationException;

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

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> searchLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> searchLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(lineService.findId(id));
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        return ResponseEntity.ok().body(lineService.update(id, lineRequest));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{lineId}/sections")
    public ResponseEntity<LineResponse> addSection(
        @PathVariable Long lineId,
        @RequestBody SectionRequest sectionRequest) {
        LineResponse lineResponse = lineService.addSections(lineId, sectionRequest);
        return ResponseEntity.ok().body(lineResponse);
    }

    @ExceptionHandler(LineDuplicateException.class)
    public ResponseEntity<ErrorMessage> handleLineDuplicateException(LineDuplicateException e) {
        return ResponseEntity.badRequest().body(ErrorMessage.of(e.getMessage()));
    }

    @ExceptionHandler(NotFoundLineException.class)
    public ResponseEntity<ErrorMessage> handleNotFoundLineException(NotFoundLineException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorMessage.of(e.getMessage()));
    }

    @ExceptionHandler(NotFoundStationException.class)
    public ResponseEntity<ErrorMessage> handleNotFoundStationException(NotFoundStationException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorMessage.of(e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorMessage.of(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorMessage> handleDefaultException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorMessage.of(DefaultException.UNEXPECTED_ERROR));
    }
}
