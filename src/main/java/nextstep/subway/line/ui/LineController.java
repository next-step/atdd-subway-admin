package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<LineResponse> create(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.save(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId()))
                .body(line);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> findAll() {
        return ResponseEntity.ok(lineService.findAll().toList());
    }

    @GetMapping("{id}")
    public ResponseEntity<LineResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(lineService.findById(id));
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody LineRequest updatedLine) {
        lineService.update(id, updatedLine);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("{id}/sections")
    public ResponseEntity<LineResponse> addSection(@PathVariable Long id, @RequestBody SectionRequest section) {
        LineResponse line = lineService.addSection(id, section);
        return ResponseEntity.ok(line);
    }

    @DeleteMapping("{id}/sections")
    public ResponseEntity<Void> removeLineStation(
            @PathVariable Long id,
            @RequestParam Long stationId) {
        lineService.removeSectionByStationId(id, stationId);
        return ResponseEntity.ok().build();
    }
}
