package nextstep.subway.ui;

import nextstep.subway.application.LineService;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lines")
public class LineStationController {

    private final LineService lineService;

    public LineStationController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<LineResponse> addLineStation(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        LineResponse result = lineService.addLineStation(lineId, sectionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<LineResponse> deleteLineStation(@PathVariable Long lineId, @RequestParam Long stationId) {
        lineService.deleteLineStation(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
