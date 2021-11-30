package nextstep.subway.line.ui;

import nextstep.subway.line.application.SectionService;
import nextstep.subway.line.dto.LineCreateResponse;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.dto.SectionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<LineCreateResponse> addSection(@PathVariable Long id, @RequestBody SectionRequest request) {
        LineCreateResponse line = sectionService.addSection(id, request);
        return ResponseEntity.created(URI.create("/lines/" + line.getId() + "/sections")).body(line);
    }

    @GetMapping("/{id}/sections")
    public ResponseEntity<List<SectionResponse>> addSection(@PathVariable Long id) {
        List<SectionResponse> section = sectionService.findSection(id);
        return ResponseEntity.ok().body(section);
    }

    @DeleteMapping(value = "/{lineId}/sections")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionService.deleteStation(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
