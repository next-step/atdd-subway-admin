package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineStationService;
import nextstep.subway.line.dto.LineStationCreateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lines/{id}/sections")
public class LineStationController {

    public LineStationController(LineStationService lineStationService){
        this.lineStationService = lineStationService;
    }

    private final LineStationService lineStationService;

    @PostMapping
    public void addSectionToLine(@PathVariable long id, @Validated @RequestBody LineStationCreateRequest request){
        lineStationService.addSectionToLine(id, request);
    }

    @DeleteMapping
    public void deleteSectionFromLine(@PathVariable long id, @RequestParam long stationId){
        lineStationService.deleteSectionFromLine(id, stationId);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgsException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
