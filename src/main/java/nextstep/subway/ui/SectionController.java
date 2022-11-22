package nextstep.subway.ui;

import nextstep.subway.application.SectionService;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class SectionController {
    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<LineResponse> createStation(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        LineResponse lineResponse = sectionService.addSection(lineId, sectionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(lineResponse);
    }
}
