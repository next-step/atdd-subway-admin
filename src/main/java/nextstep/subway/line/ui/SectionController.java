package nextstep.subway.line.ui;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.line.application.SectionService;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;

@RestController
@RequestMapping("/lines")
public class SectionController {
    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<LineResponse> addSection(@PathVariable Long lineId, @RequestBody SectionRequest request) {
        LineResponse response = sectionService.addSection(request, lineId);
        return ResponseEntity.created(URI.create("/lines/" + response.getId())).body(response);
    }
}
