package nextstep.subway.line.ui;

import nextstep.subway.common.ui.ErrorResponse;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new LineRequestValidator());
    }

    @PostMapping
    public ResponseEntity<?> createLine(@RequestBody @Validated LineRequest lineRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorResponse(bindingResult));
        }
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> getLines() {
        List<LineResponse> lines = lineService.getLines();
        return ResponseEntity.ok(lines);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
        LineResponse line = lineService.getLine(id);
        return ResponseEntity.ok(line);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> modifyLine(@PathVariable Long id, @RequestBody @Valid LineRequest lineRequest) {
        LineResponse line = lineService.modifyLine(id, lineRequest);
        return ResponseEntity.ok(line);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }
}
