package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {
    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> findAllLines() {
        List<LineResponse> lines = lineService.findAllLines();
        return ResponseEntity.ok().body(lines);
    }

    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity findLine(@PathVariable Long id) {
        LineResponse line = lineService.findLine(id);
        return ResponseEntity.ok().body(line);
    }

    @PutMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity editLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.editLine(id, lineRequest);
        LineResponse line = lineService.findLine(id);
        return ResponseEntity.ok().body(line);
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity addSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        lineService.addSection(lineId, sectionRequest);
        LineResponse line = lineService.findLine(lineId);
        return ResponseEntity.ok().body(line);
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity removeLineStation(@PathVariable Long lineId, @RequestParam Long stationId) {
        lineService.removeSectionByStationId(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
