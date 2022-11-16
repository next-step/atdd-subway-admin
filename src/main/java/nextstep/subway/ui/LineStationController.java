package nextstep.subway.ui;

import java.util.List;
import javax.persistence.PersistenceException;
import nextstep.subway.application.LineStationService;
import nextstep.subway.dto.LineStationResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/line-station")
public class LineStationController {

    private final LineStationService lineStationService;

    public LineStationController(LineStationService lineStationService) {
        this.lineStationService = lineStationService;
    }

    @PostMapping("/{lineId}")
    public ResponseEntity addSection(@PathVariable("lineId") Long lineId,
                                     @RequestBody SectionRequest sectionRequest) {
        lineStationService.addSection(lineId, sectionRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{lineId}")
    public ResponseEntity<List<LineStationResponse>> showLineStations(@PathVariable("lineId") Long lineId) {
        return ResponseEntity.ok().body(lineStationService.findLineStationsByLineId(lineId));
    }

    @ExceptionHandler({PersistenceException.class, IllegalArgumentException.class})
    public ResponseEntity handleIllegalArgsException() {
        return ResponseEntity.badRequest().build();
    }
}
