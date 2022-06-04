package nextstep.subway.ui;

import nextstep.subway.application.LineService;
import nextstep.subway.application.LineStationService;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineStationRequest;
import nextstep.subway.dto.LineStationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("line")
public class LineController {
    private final LineService lineService;
    private LineStationService lineStationService;

    public LineController(LineService lineService, LineStationService lineStationService) {
        this.lineService = lineService;
        this.lineStationService = lineStationService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse lineCreationResponse = lineService.createLine(lineRequest);
        return ResponseEntity.created(URI.create("/line")).body(lineCreationResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> getLines() {
        List<LineResponse> lineListResponse = lineService.getLines();
        return ResponseEntity.ok(lineListResponse);
    }

    @PutMapping("{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        LineResponse lineUpdateResponse = lineService.updateLine(id, lineRequest);
        return ResponseEntity.ok(lineUpdateResponse);
    }

    @GetMapping("{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
        LineResponse lineResponse = lineService.getLine(id);
        return ResponseEntity.ok(lineResponse);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("{id}/section")
    public ResponseEntity<LineStationResponse> createLineStation(@PathVariable Long id, @RequestBody LineStationRequest lineStationRequest) {
        LineStationResponse lineStationResponse = lineStationService.createLineStation(lineStationRequest);
        return ResponseEntity.created(URI.create("/" + id + "/section")).body(lineStationResponse);
    }
}
