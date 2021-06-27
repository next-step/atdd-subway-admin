package nextstep.subway.line.ui;

import nextstep.subway.line.application.SectionToLineAddService;
import nextstep.subway.line.dto.LineStationCreateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lines/{id}/sections")
public class LineStationController {

    public LineStationController(SectionToLineAddService sectionToLineAddService){
        this.sectionToLineAddService = sectionToLineAddService;
    }

    private final SectionToLineAddService sectionToLineAddService;

    @PostMapping
    public void addSectionToLine(@PathVariable long id, @Validated @RequestBody LineStationCreateRequest request){
        sectionToLineAddService.addSectionToLine(id, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgsException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
