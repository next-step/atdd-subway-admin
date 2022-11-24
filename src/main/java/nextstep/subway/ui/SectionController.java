package nextstep.subway.ui;

import nextstep.subway.application.SectionService;
import nextstep.subway.application.StationService;
import nextstep.subway.dto.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class SectionController {

    private SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/lines/{id}/sections")
    public ResponseEntity createSection(@RequestBody SectionRequest sectionRequest,
                                        @PathVariable Long id) {
        LineResponse line = sectionService.saveSection(sectionRequest, id);
        return ResponseEntity.created(URI.create("/sections/" + line.getId())).body(line);
    }

    @GetMapping("/sections/{id}")
    public ResponseEntity<SectionResponse> getSection(@PathVariable Long id) {
        return ResponseEntity.ok().body(sectionService.findById(id));
    }

    @DeleteMapping("/lines/{id}/sections")
    public ResponseEntity removeSection(@RequestParam Long stationId,
                                        @PathVariable Long id) {
        sectionService.deleteSection(stationId, id);
        return ResponseEntity.noContent().build();
    }

}
