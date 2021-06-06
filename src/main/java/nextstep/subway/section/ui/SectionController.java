package nextstep.subway.section.ui;

import java.net.URI;
import nextstep.subway.line.application.LineCommandService;
import nextstep.subway.line.application.LineQueryService;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.application.SectionCommandService;
import nextstep.subway.section.dto.SectionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/lines/{lineId}")
public class SectionController {

    private final LineQueryService lineQueryService;
    private final LineCommandService lineCommandService;
    private final SectionCommandService sectionCommandService;

    public SectionController(LineQueryService lineQueryService,
                             LineCommandService lineCommandService,
                             SectionCommandService sectionCommandService) {
        this.lineQueryService = lineQueryService;
        this.lineCommandService = lineCommandService;
        this.sectionCommandService = sectionCommandService;
    }

    @PostMapping("/sections")
    public ResponseEntity<LineResponse> addSection(@PathVariable Long lineId,
                                                   @RequestBody SectionRequest request) {

        Long sectionId = sectionCommandService.save(request.getUpStationId(),
                                                    request.getDownStationId(),
                                                    request.getDistance());

        lineCommandService.addSection(lineId, sectionId);

        return ResponseEntity.created(URI.create("/lines/" + lineId))
                             .body(LineResponse.of(lineQueryService.findById(lineId)));
    }
}
