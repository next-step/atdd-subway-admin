package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity modifyLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.modifyLine(id, lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> findAllLines() {
        return ResponseEntity.ok(lineService.findAllLines());
    }

    @GetMapping("/{id}")
    public ResponseEntity findLine(@PathVariable Long id) {
        return ResponseEntity.ok(lineService.findLine(id));
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity sections(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        LineResponse line = lineService.addSections(id, sectionRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity removeLineStation(@PathVariable Long lineId, @RequestParam Long stationId) {
        lineService.deleteSection(lineId, stationId);
        return ResponseEntity.ok().build();
    }

}
