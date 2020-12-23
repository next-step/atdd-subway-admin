package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.application.exceptions.LineNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.exceptions.InvalidSectionException;
import nextstep.subway.line.domain.exceptions.StationNotFoundException;
import nextstep.subway.line.domain.exceptions.TargetSectionNotFoundException;
import nextstep.subway.line.domain.exceptions.TooLongSectionException;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

    @PostMapping("/{lineId}/sections")
    public ResponseEntity addSection(
            @PathVariable("lineId") Long lineId,
            @RequestBody SectionRequest sectionRequest
    ) {
        boolean addSectionResult = lineService.addSection(lineId, sectionRequest);

        if (addSectionResult) {
            return ResponseEntity.created(URI.create("/lines/" + lineId)).build();
        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping
    public ResponseEntity createLine(@Validated @RequestBody LineRequest lineRequest) {
        LineResponse lineResponse = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping
    public ResponseEntity getLines() {
        List<LineResponse> lineResponses = lineService.getAllLines();
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/{lineId}")
    public ResponseEntity getLine(
            @PathVariable("lineId") Long lineId
    ) {
        LineResponse lineResponse = lineService.getLine(lineId);
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping("/{lineId}")
    public ResponseEntity updateLine(
            @PathVariable("lineId") Long lineId,
            @Validated @RequestBody LineRequest lineRequest
    ) {
        Line line = lineService.updateLine(lineId, lineRequest.getName(), lineRequest.getColor());
        return ResponseEntity.ok(URI.create("/lines/" + line.getId()));
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity deleteLine(
            @PathVariable("lineId") Long lineId
    ) {
        lineService.deleteLine(lineId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(LineNotFoundException.class)
    public ResponseEntity handleLineNotFoundException(LineNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(InvalidSectionException.class)
    public ResponseEntity handleInvalidSectionException(InvalidSectionException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(StationNotFoundException.class)
    public ResponseEntity handleStationNotFoundException(StationNotFoundException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(TooLongSectionException.class)
    public ResponseEntity handleTooLongSectionException(TooLongSectionException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(TargetSectionNotFoundException.class)
    public ResponseEntity handleTargetSectionNotFoundException(TargetSectionNotFoundException e) {
        return ResponseEntity.badRequest().build();
    }
}
