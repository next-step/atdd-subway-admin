package nextstep.subway.line.ui;

import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public ResponseEntity getLines() {
        LinesResponse lines = lineService.getLines();
        return ResponseEntity.ok().body(lines);
    }

    @GetMapping
    @RequestMapping("/{id}")
    public ResponseEntity getLine(@PathVariable("id") Long id) {
        try {
            LineResponse line = lineService.getLine(id);
            return ResponseEntity.ok(line);
        }catch (NotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }
}
