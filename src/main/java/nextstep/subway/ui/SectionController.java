package nextstep.subway.ui;

import java.net.URI;
import java.util.List;
import nextstep.subway.application.LineService;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SectionController {
    private final LineService lineService;

    public SectionController(LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping("lines/{lineId}/sections")
    public ResponseEntity<List<SectionResponse>> searchSection(@PathVariable Long lineId) {
        return ResponseEntity.ok()
                .body(lineService.findAllSection(lineId));
    }

    @PostMapping("{lineId}/selections")
    public ResponseEntity addSection(@PathVariable Long lineId,
                                     @RequestBody SectionRequest sectionRequest) {

        lineService.saveSection(lineId, sectionRequest);
        return ResponseEntity.created(URI.create("lines/" + lineId + "/sections")).build();
    }

    @DeleteMapping("{lineId}/selections")
    public ResponseEntity addSection(@PathVariable Long lineId,
                                     @RequestParam Long stationId) {

        lineService.deleteSection(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
