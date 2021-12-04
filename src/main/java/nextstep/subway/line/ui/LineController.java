package nextstep.subway.line.ui;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.application.SectionService;
import nextstep.subway.section.dto.SectionRequest;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;
    private final SectionService sectionService;

    public LineController(final LineService lineService,
        SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId()))
            .body(line);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> getLines() {
        List<LineResponse> lines = lineService.getLines();
        return ResponseEntity.ok(lines);
    }

    @GetMapping("{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable long id) {
        LineResponse line = lineService.getLine(id);
        return ResponseEntity.ok(line);
    }

    @PutMapping("{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable long id, @RequestBody LineRequest lineRequest) {
        LineResponse lineResponse = lineService.updateLine(id, lineRequest);
        return ResponseEntity.ok()
            .body(lineResponse);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent()
            .build();
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<LineResponse> addSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        LineResponse lineResponse = sectionService.saveSection(lineId, sectionRequest);
        return ResponseEntity.ok()
            .body(lineResponse);
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<Void> removeLineStation(@PathVariable long lineId, @RequestParam long stationId) {
        sectionService.removeSectionByStationId(lineId, stationId);
        return ResponseEntity.noContent()
            .build();
    }
}
