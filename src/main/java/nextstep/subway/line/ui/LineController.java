package nextstep.subway.line.ui;

import static org.springframework.http.MediaType.*;
import static org.springframework.http.ResponseEntity.*;

import nextstep.subway.line.application.DuplicationKeyException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;
    private Long id;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping(consumes = {APPLICATION_JSON_VALUE})
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping(consumes = {APPLICATION_JSON_VALUE})
    public ResponseEntity<List<LineResponse>> findAll(Pageable pageable) {
        List<LineResponse> lineAll = lineService.findAllLine(pageable);
        return ResponseEntity.ok(lineAll);
    }

    @GetMapping(value ="{id}",consumes = {APPLICATION_JSON_VALUE})
    public ResponseEntity<LineResponse> findLineById(final @PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(lineService.findLineById(id));
    }

    @ExceptionHandler(DuplicationKeyException.class)
    public ResponseEntity handleIllegalArgsException(DuplicationKeyException e) {
        return ResponseEntity.badRequest().build();
    }
}
