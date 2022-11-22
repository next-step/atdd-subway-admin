package nextstep.subway.ui;

import nextstep.subway.application.LineService;
import nextstep.subway.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {
    private LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineSaveRequest lineSaveRequest) {
        LineResponse lineResponse = lineService.saveStation(lineSaveRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> findAllLine() {
        return ResponseEntity.ok().body(lineService.findAllLine());
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> findLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(lineService.findById(id));
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineUpdateRequest lineUpdateRequest) {
        lineService.updateLine(id, lineUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<LineResponse> createSection(@PathVariable Long lineId, @RequestBody SectionSaveRequest sectionRequest) {
        LineResponse lineResponse = lineService.createSection(lineId, sectionRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping("/lines/{lineId}/sections")
    public ResponseEntity<SectionsResponse> createSection(@PathVariable Long lineId) {
        SectionsResponse sectionsResponse = lineService.findSectionsByLine(lineId);
        return ResponseEntity.ok(sectionsResponse);
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<SectionsResponse> deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        lineService.deleteSection(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
