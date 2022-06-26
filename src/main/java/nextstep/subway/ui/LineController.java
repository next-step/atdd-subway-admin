package nextstep.subway.ui;

import nextstep.subway.application.LineService;
import nextstep.subway.dto.*;
import nextstep.subway.error.ApiExceptionResponse;
import nextstep.subway.error.SubwayError;
import nextstep.subway.error.exception.SubWayApiException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<Void> addSection(@PathVariable("id") Long lineId,
                                         @RequestBody SectionRequest sectionRequest) {
        lineService.addSection(lineId, sectionRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> getStation(@PathVariable Long id) {
        LineResponse lineResponse = lineService.getLineById(id);
        return ResponseEntity.ok(lineResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> updateStation(@PathVariable Long id,
                                                      @RequestBody LineUpdateRequest lineUpdateRequest) {
        LineResponse line = lineService.updateLineById(id, lineUpdateRequest);
        return ResponseEntity.ok(line);
    }

    @ExceptionHandler(SubWayApiException.class)
    public ResponseEntity<ApiExceptionResponse> handleSubWayApiException(SubWayApiException e) {
        return ResponseEntity.badRequest()
                .body(new ApiExceptionResponse(e.getError(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiExceptionResponse> handleException(Exception e) {
        return ResponseEntity.badRequest()
                .body(new ApiExceptionResponse(SubwayError.NOT_DEFINED, e.getMessage()));
    }
}
