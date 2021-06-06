package nextstep.subway.section.ui;

import java.net.URI;
import nextstep.subway.line.application.LineCommandService;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.application.SectionCommandService;
import nextstep.subway.section.dto.SectionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class SectionController {

    private final SectionCommandService sectionCommandService;

    public SectionController(SectionCommandService sectionCommandService) {
        this.sectionCommandService = sectionCommandService;
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<LineResponse> addSection(@PathVariable Long lineId,
                                                   @RequestBody SectionRequest sectionRequest) {
//        LineResponse line = sectionCommandService.addSection(lineId, sectionRequest);
//        return ResponseEntity.created(URI.create("/lines/" + lineId)).body(line);
        return null;
    }
}
