package nextstep.subway.section.ui;

import nextstep.subway.section.application.SectionService;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class SectionController {
    @Autowired
    private SectionService sectionService;

    @PostMapping("/sections")
    public ResponseEntity<SectionResponse> create(@RequestBody SectionRequest request) {
        SectionResponse response = sectionService.saveSection(request);
        return ResponseEntity.created(URI.create("/sections/" + response.getId())).body(response);
    }

    @GetMapping("/sections/{lineId}")
    public ResponseEntity<List<SectionResponse>> selectAll(@PathVariable Long lineId) {
        List<SectionResponse> responses = sectionService.findAll(lineId);
        return ResponseEntity.ok(responses);
    }
}
