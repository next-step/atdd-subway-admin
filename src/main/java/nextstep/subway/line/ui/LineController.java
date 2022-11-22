package nextstep.subway.line.ui;

import java.net.URI;
import java.util.List;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.dto.SectionResponse;
import nextstep.subway.line.dto.UpdateLineRequest;
import nextstep.subway.line.exception.IllegalDistanceException;
import nextstep.subway.line.exception.NoRelationStationException;
import nextstep.subway.line.exception.SameStationException;
import nextstep.subway.line.exception.SingleSectionException;
import nextstep.subway.line.exception.StationNotExistException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.createLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> getLines() {
        return ResponseEntity.ok(lineService.getLines());
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
        return ResponseEntity.ok(lineService.getLine(id));
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id,
        @RequestBody UpdateLineRequest request) {
        lineService.updateLine(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{id}/sections")
    public ResponseEntity<Void> createSection(@PathVariable Long id,
        @RequestBody SectionRequest request) {
        lineService.addSection(id, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/lines/{id}/sections")
    public ResponseEntity<List<SectionResponse>> getSections(@PathVariable Long id) {
        return ResponseEntity.ok().body(lineService.getSections(id));
    }

    @DeleteMapping("/lines/{id}/sections")
    public ResponseEntity<Void> removeLineStation(
        @PathVariable Long id,
        @RequestParam Long stationId) {
        lineService.removeSectionByStationId(id, stationId);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(IllegalDistanceException.class)
    public ResponseEntity<String> illegalDistanceExceptionHandler(
        final IllegalDistanceException exception) {
        return ResponseEntity.badRequest()
            .body(exception.getMessage());
    }

    @ExceptionHandler(SameStationException.class)
    public ResponseEntity<String> sameStationExceptionHandler(
        final SameStationException exception) {
        return ResponseEntity.badRequest()
            .body(exception.getMessage());
    }

    @ExceptionHandler(NoRelationStationException.class)
    public ResponseEntity<String> noRelationStationExceptionHandler(
        final NoRelationStationException exception) {
        return ResponseEntity.badRequest()
            .body(exception.getMessage());
    }

    @ExceptionHandler(SingleSectionException.class)
    public ResponseEntity<String> singleSectionExceptionHandler(
        final SingleSectionException exception) {
        return ResponseEntity.badRequest()
            .body(exception.getMessage());
    }

    @ExceptionHandler(StationNotExistException.class)
    public ResponseEntity<String> stationNotExistExceptionHandler(
        final StationNotExistException exception) {
        return ResponseEntity.badRequest()
            .body(exception.getMessage());
    }
}
