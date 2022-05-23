package nextstep.subway.ui;

import nextstep.subway.application.SectionService;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
