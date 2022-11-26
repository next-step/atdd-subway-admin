package nextstep.subway.ui;

import nextstep.subway.application.SectionService;
import nextstep.subway.dto.SectionRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lines")
public class SectionController {
    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }


    @PostMapping("/{lineId}/sections")
    public ResponseEntity<Void> registerSection(
            @PathVariable Long lineId,
            @RequestBody SectionRequest sectionRequest) {
        sectionService.registerSection(lineId, sectionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<Void> removeSection(
            @PathVariable Long lineId,
            @RequestParam Long stationId) {
        sectionService.removeSection(lineId, stationId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ExceptionHandler({DataIntegrityViolationException.class, IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Void> handleIllegalArgsException() {
        return ResponseEntity.badRequest().build();
    }
}
