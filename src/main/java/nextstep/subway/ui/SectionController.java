package nextstep.subway.ui;

import nextstep.subway.application.LineService;
import nextstep.subway.application.SectionService;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class SectionController {
    private final LineService lineService;
    private final SectionService sectionService;

    public SectionController(LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping("/lines/{id}/sections")
    public ResponseEntity<LineResponse> createSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        LineResponse line = sectionService.saveSection(lineService.findById(id), sectionRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @DeleteMapping("/lines/{id}/sections")
    public ResponseEntity<LineResponse> removeLineStation(@PathVariable Long id, @RequestParam Long stationId) {
        lineService.removeSectionByStationId(id, stationId);

        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handleIllegalArgsException() {
        return ResponseEntity.badRequest().build();
    }
}
