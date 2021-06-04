package nextstep.subway.line.ui;

import nextstep.subway.line.application.AlreadyExistsLineNameException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> findLines() {
        List<LineResponse> lineResponses = LineResponse.allOf(lineService.findAllLines());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/{lineId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> findLine(@PathVariable long lineId) {
        Line line = lineService.findLine(lineId);
        return ResponseEntity.ok().body(LineResponse.of(line));
    }

    @ExceptionHandler({AlreadyExistsLineNameException.class})
    public ResponseEntity<String> handleDuplicateException(
        AlreadyExistsLineNameException alreadyExistsLineNameException) {
        String message = alreadyExistsLineNameException.getMessage();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
    }
}
