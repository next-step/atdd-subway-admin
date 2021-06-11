package nextstep.subway.line.ui;

import nextstep.subway.line.application.SectionService;
import nextstep.subway.line.dto.SectionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/lines")
public class SectionController {
    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity addSection(@PathVariable Long lineId,
                                     @RequestBody SectionRequest sectionRequest) {
        sectionService.addSection(lineId, sectionRequest);
        return ResponseEntity.created(URI.create(lineId + "/sections")).body(HttpStatus.CREATED);
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity removeSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionService.removeSection(lineId, stationId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleIllegalArgsExceptionForSection(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().build();
    }
}
