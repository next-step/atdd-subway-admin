package nextstep.subway.ui;

import java.util.List;
import javax.persistence.PersistenceException;
import nextstep.subway.application.SectionService;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/section")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/{lineId}")
    public ResponseEntity addSection(@PathVariable("lineId") Long lineId,
                                     @RequestBody SectionRequest sectionRequest) {
        sectionService.addSection(lineId, sectionRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{lineId}")
    public ResponseEntity<List<SectionResponse>> showSections(@PathVariable("lineId") Long lineId) {
        return ResponseEntity.ok().body(sectionService.findSectionsByLineId(lineId));
    }

    @ExceptionHandler({PersistenceException.class, IllegalArgumentException.class})
    public ResponseEntity handleIllegalArgsException() {
        return ResponseEntity.badRequest().build();
    }
}
