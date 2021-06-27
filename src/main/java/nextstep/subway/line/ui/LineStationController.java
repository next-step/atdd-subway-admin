package nextstep.subway.line.ui;

import nextstep.subway.line.application.SectionToLineAddService;
import nextstep.subway.line.dto.LineStationCreateRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lines/{id}/sections")
public class LineStationController {

    public LineStationController(SectionToLineAddService sectionToLineAddService){
        this.sectionToLineAddService = sectionToLineAddService;
    }

    private final SectionToLineAddService sectionToLineAddService;

    @PostMapping
    public void addSectionToLine(@PathVariable long id, @RequestBody LineStationCreateRequest request){
        sectionToLineAddService.addSectionToLine(id, request);
    }
}
