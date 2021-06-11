package nextstep.subway.section.ui;

import nextstep.subway.section.application.SectionService;
import nextstep.subway.section.dto.SectionRequest;
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

    @PostMapping("/{id}/sections")
    public ResponseEntity createSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        sectionService.saveSection(id, sectionRequest);
        return ResponseEntity.created(URI.create("/lines/" + id + "/sections")).body(null);
    }
}
