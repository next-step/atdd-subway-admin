package nextstep.subway.section.ui;

import java.net.URI;
import java.util.List;
import nextstep.subway.line.application.LineCommandService;
import nextstep.subway.line.application.LineQueryService;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.application.SectionCommandService;
import nextstep.subway.section.domain.LineSections;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.util.stream.Collectors.toList;

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

        LineSections sections = lineQueryService.findById(lineId).getSections();
        List<Long> sectionIds = sections.getSections()
                                        .stream()
                                        .map(Section::getId)
                                        .collect(toList());

        lineCommandService.updateSections(lineId,
                                          sectionCommandService.upsert(sectionIds,
                                                                       request.getUpStationId(),
                                                                       request.getDownStationId(),
                                                                       request.getDistance()));

        return ResponseEntity.created(URI.create("/lines/" + lineId))
                             .body(LineResponse.of(lineQueryService.findById(lineId)));
    }
}
