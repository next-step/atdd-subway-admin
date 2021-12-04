package nextstep.subway.line.ui;

import java.net.URI;
import nextstep.subway.line.application.SectionService;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.dto.SectionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lines/{lineId}")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(final SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/sections")
    public ResponseEntity<Void> addSection(
        @PathVariable final Long lineId,
        @RequestBody final SectionRequest request
    ) {
        final SectionResponse response = sectionService.addSection(lineId, request);
        return ResponseEntity.created(
            URI.create("/lines/" + lineId + "/sections/" + response.getId())
        ).build();
    }
}
