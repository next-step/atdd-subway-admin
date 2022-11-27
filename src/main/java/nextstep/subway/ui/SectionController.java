package nextstep.subway.ui;

import nextstep.subway.application.SectionService;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SectionController {
    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<SectionResponse> createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        SectionResponse sectionResponse = sectionService.addSection(lineId, sectionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(sectionResponse);
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionService.deleteSection(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
