package nextstep.subway.section.ui;

import nextstep.subway.section.application.SectionService;
import nextstep.subway.section.dto.SectionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> addSection(@PathVariable Long lineId, @RequestBody SectionRequest request) {
        sectionService.addSection(lineId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> addSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        return ResponseEntity.ok().build();
    }
}
