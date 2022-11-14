package nextstep.subway.ui;

import nextstep.subway.application.LineStationService;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineStationRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lines")
public class LineStationController {
    private LineStationService lineStationService;

    public LineStationController(LineStationService lineStationService){
        this.lineStationService = lineStationService;
    }

    @PostMapping("/{lineId}/stations")
    public ResponseEntity<LineResponse> createLineStation(@PathVariable Long lineId, @RequestBody LineStationRequest lineStationRequest) {
        LineResponse line = lineStationService.addLineStation(lineId, lineStationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(line);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Void> handleDataIntegrityViolationException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> handleIllegalArgsException() {
        return ResponseEntity.badRequest().build();
    }

}
