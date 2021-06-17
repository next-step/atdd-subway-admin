package nextstep.subway.line.ui;

import nextstep.subway.line.application.SectionCommandUseCase;
import nextstep.subway.line.dto.SectionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lines")
public class SectionController {
    private final SectionCommandUseCase sectionCommandUseCase;

    public SectionController(SectionCommandUseCase sectionCommandUseCase) {
        this.sectionCommandUseCase = sectionCommandUseCase;
    }

    @PostMapping("/{lineId:\\d+}/sections")
    public ResponseEntity<Void> createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        sectionCommandUseCase.addSection(lineId, sectionRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{lineId:\\d+}/sections")
    public ResponseEntity<Void> removeSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionCommandUseCase.removeSectionByStationId(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
