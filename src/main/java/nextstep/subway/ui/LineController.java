package nextstep.subway.ui;

import nextstep.subway.application.LineService;
import nextstep.subway.dto.LineCreateRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public LineResponse createLine(@RequestBody LineCreateRequest request) {
        return lineService.createLine(request);
    }

    @GetMapping
    public List<LineResponse> findLines() {
        return lineService.findLines();
    }

    @GetMapping("/{id}")
    public LineResponse findLine(@PathVariable Long id) {
        return lineService.findLine(id);
    }

    @PutMapping("/{id}")
    public void updateLine(@PathVariable Long id, @RequestBody LineUpdateRequest request) {
        lineService.updateLine(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
    }
}
