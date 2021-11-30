package nextstep.subway.section.ui;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.application.SectionService;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class SectionController {
    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<SectionResponse> addSection(
            @PathVariable Long lineId,
            @RequestBody SectionRequest sectionRequest) {
        SectionResponse sectionResponse = sectionService.saveSection(sectionRequest, lineId);
        return ResponseEntity.created(URI.create("/lines/" + lineId)).body(sectionResponse);
    }

    @GetMapping(value = "/lines/{lineId}/sections", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SectionResponse>> showSections(@PathVariable Long lineId) {
        return ResponseEntity.ok().body(sectionService.findAllSectionsByLine(lineId));
    }
}
