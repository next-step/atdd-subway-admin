package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineStationService;
import nextstep.subway.line.dto.LineStationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lines")
public class LineStationController {

    private final LineStationService lineStationService;

    public LineStationController(LineStationService lineStationService) {
        this.lineStationService = lineStationService;
    }


    @PostMapping("{id}/sections")
    public ResponseEntity<Boolean> createLineStation(@PathVariable Long id,
        @RequestBody LineStationRequest lineStationRequest) {
        lineStationService.createLineStation(id, lineStationRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @DeleteMapping("{id}/sections")
    public ResponseEntity<Boolean> deleteStation(@PathVariable Long id,
        @RequestParam Long stationId) {
        lineStationService.removeSectionByStationId(id, stationId);
        return ResponseEntity.noContent().build();
    }
}
