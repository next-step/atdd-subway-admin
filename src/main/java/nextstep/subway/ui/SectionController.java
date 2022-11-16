package nextstep.subway.ui;

import nextstep.subway.application.SectionService;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class SectionController {

    private SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<SectionResponse> createLine(@PathVariable("lineId") Long lineId, @RequestBody SectionRequest request) {
        SectionResponse response = sectionService.createSection(lineId, request);
        return ResponseEntity.created(URI.create("/lines/" + response.getId() + "/sections")).body(response);
    }
}
