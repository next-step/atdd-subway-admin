package nextstep.subway.ui;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.application.LineService;
import nextstep.subway.dto.ErrorResponse;
import nextstep.subway.dto.LineChange;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.exception.AlreadyDeletedException;
import nextstep.subway.exception.BadRequestException;
import nextstep.subway.exception.NotFoundException;

@RestController
public class LineController {
    private LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createStation(@RequestBody LineRequest lineRequest) {
        LineResponse lineResponse = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping(value = "/lines")
    public ResponseEntity<List<LineResponse>> fetchLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> fetchLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(lineService.findLine(id));
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<?> changeLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.changeLine(id, LineChange.of(lineRequest));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<?> removeLine(@PathVariable Long id) {
        lineService.removeLine(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(AlreadyDeletedException.class)
    public ResponseEntity<?> handleAlreadyDeletedException() {
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler
    public ResponseEntity<?> handleBadRequestException(BadRequestException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }
}
