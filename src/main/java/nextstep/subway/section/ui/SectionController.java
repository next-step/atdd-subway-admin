package nextstep.subway.section.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class SectionController {

    private final LineService lineService;

    public SectionController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping(name = "lines/{lineId}/sections")
    public ResponseEntity<LineResponse> registerSections(
            @PathVariable Long lineId,
            @RequestBody SectionRequest request
    ) {
        final Line line = lineService.addSection(request);
        return ResponseEntity.ok(LineResponse.of(line));
    }

    @DeleteMapping(name = "lines/{lineId}/sections/{sectionId}")
    public ResponseEntity<LineResponse> deleteSection(
            @PathVariable Long lineId,
            @PathVariable Long sectionId,
            @RequestBody SectionRequest request
    ) {
        return null;
    }

}
