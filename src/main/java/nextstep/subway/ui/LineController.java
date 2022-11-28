package nextstep.subway.ui;

import nextstep.subway.application.LineService;
import nextstep.subway.dto.LineCreateRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.dto.SectionCreateRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/{id}/sections")
    public void addSection(@PathVariable Long id, @RequestBody SectionCreateRequest request) {
        lineService.addSection(id, request);
    }

    @DeleteMapping("/{id}/sections")
    public void deleteSection(@PathVariable("id") Long lineId, @RequestParam("stationId") Long stationId) {
        lineService.deleteSection(lineId, stationId);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleIllegalArgsException(Exception e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
