package nextstep.subway.ui;

import nextstep.subway.application.LineStationService;
import nextstep.subway.dto.SectionRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lines")
public class LineStationController {
    private final LineStationService lineStationService;

    public LineStationController(LineStationService lineStationService) {
        this.lineStationService = lineStationService;
    }


    @PostMapping("/{id}/sections")
    public ResponseEntity<Void> addLineStation(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        lineStationService.addLineStation(id, sectionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ExceptionHandler({DataIntegrityViolationException.class, IllegalArgumentException.class})
    public ResponseEntity handleIllegalArgsException() {
        return ResponseEntity.badRequest().build();
    }
}
