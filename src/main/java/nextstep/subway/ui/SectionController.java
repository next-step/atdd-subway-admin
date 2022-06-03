package nextstep.subway.ui;

import nextstep.subway.application.SectionService;
import nextstep.subway.dto.SectionResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SectionController {
    private SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @GetMapping(value = "/sections/{lineId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SectionResponses> showLineSections(@PathVariable Long lineId) {
        return ResponseEntity.ok().body(sectionService.findAllSections(lineId));
    }
}
