package nextstep.subway.section.ui;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lines")
public class SectionController {

    @PostMapping(name = "{lineId}/sections")
    public ResponseEntity<LineResponse> registerSections(
            @PathVariable Long lineId,
            @RequestBody SectionRequest request
    ) {
        return null;
    }

    @DeleteMapping(name = "{lineId}/sections/{sectionId}")
    public ResponseEntity<LineResponse> deleteSection(
            @PathVariable Long lineId,
            @PathVariable Long sectionId,
            @RequestBody SectionRequest request
    ) {
        return null;
    }

}
