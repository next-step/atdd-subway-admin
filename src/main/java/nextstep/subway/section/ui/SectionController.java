package nextstep.subway.section.ui;

import nextstep.subway.section.application.SectionCommandService;
import nextstep.subway.section.dto.SectionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lines")
public class SectionController {
    private final SectionCommandService sectionCommandService;

    public SectionController(SectionCommandService sectionCommandService) {
        this.sectionCommandService = sectionCommandService;
    }

    @PostMapping(value = "/{lineId}/sections")
    public ResponseEntity<Void> addSection(@PathVariable(name = "lineId") Long lineId,
                                           @RequestBody SectionRequest sectionRequest) {
        sectionCommandService.addSection(lineId, sectionRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{lineId}/sections")
    public ResponseEntity<Void> removeSection(@PathVariable(name = "lineId") Long lineId,
                                              @RequestParam(name = "stationId") Long stationId) {
        sectionCommandService.removeSection(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
