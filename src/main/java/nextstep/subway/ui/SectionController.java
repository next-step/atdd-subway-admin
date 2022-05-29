package nextstep.subway.ui;

import nextstep.subway.application.SectionService;
import nextstep.subway.dto.request.SectionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping(value = "/lines/{lineId}/sections")
    public ResponseEntity saveSection(@PathVariable Long lineId,
        @RequestBody SectionRequest sectionRequest) {
        sectionService.createSection(lineId, sectionRequest);
        return ResponseEntity.ok().build();
    }
}
