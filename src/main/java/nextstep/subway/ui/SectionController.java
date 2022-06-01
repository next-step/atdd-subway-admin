package nextstep.subway.ui;

import java.net.URI;
import nextstep.subway.application.SectionService;
import nextstep.subway.domain.Line;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping(value = "/lines/{lineId}/sections", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        Line line = sectionService.createSection(lineId, sectionRequest);
        return ResponseEntity
                .created(URI.create("/lines/" + line.getId()))
                .body(LineResponse.of(line));
    }

    @DeleteMapping(value = "/lines/{lineId}/sections")
    public ResponseEntity<Void> createSection(@PathVariable Long lineId, @RequestParam(name = "stationId") Long stationId) {
        sectionService.deleteSection(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
