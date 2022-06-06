package nextstep.subway.ui;

import java.net.URI;
import java.util.List;
import nextstep.subway.application.SectionService;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SectionController {
    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<SectionResponse> addSection(@PathVariable Long lineId,
                                                      @RequestBody SectionRequest sectionRequest) {
        SectionResponse response = sectionService.addSection(lineId, sectionRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineId + "/sections/")).body(response);
    }

    @GetMapping(value = "/lines/{lineId}/sections", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SectionResponse>> showLineSections(@PathVariable Long lineId) {
        return ResponseEntity.ok().body(sectionService.findLineSections(lineId));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handleIllegalArgsException() {
        return ResponseEntity.badRequest().build();
    }
}
