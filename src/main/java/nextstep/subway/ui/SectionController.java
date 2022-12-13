package nextstep.subway.ui;

import java.net.URI;
import nextstep.subway.application.SectionService;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lines")
public class SectionController {
    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<LineResponse> addSection(@PathVariable Long id,
        @RequestBody SectionRequest sectionRequest) {
        LineResponse line = sectionService.addSection(id, sectionRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<Void> removeSection(@PathVariable Long lineId,
        @RequestParam Long stationId) {
        sectionService.removeSectionByStationId(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
