package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesSubResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Validated
@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) throws NoSuchFieldException {
        lineService.validateDuplicatedName(lineRequest);
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping("/{lineId}")
    public ResponseEntity readLine(@PathVariable Long lineId) {
        LinesSubResponse linesSubResponse = lineService.readLine(lineId);
        return ResponseEntity.ok(linesSubResponse);
    }

    @GetMapping
    public ResponseEntity readLineAll() {
        List<LinesSubResponse> linesSubResponses = lineService.readLineAll();
        return ResponseEntity.ok(linesSubResponses);
    }

    @PutMapping("/{lineId}")
    public ResponseEntity changeLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
        lineService.changeLine(lineId, lineRequest);
        return ResponseEntity.ok().build();
    }
}
