package nextstep.subway.line.ui;

import java.net.URI;
import java.util.List;
import nextstep.subway.line.application.LineCommandService;
import nextstep.subway.line.application.LineQueryService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineCommandService lineCommandService;
    private final LineQueryService lineQueryService;

    public LineController(LineCommandService lineCommandService,
        LineQueryService lineQueryService) {
        this.lineCommandService = lineCommandService;
        this.lineQueryService = lineQueryService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest request) {

        Long lineId = lineCommandService.save(request.toLine(),
            request.getUpStationId(),
            request.getDownStationId(),
            request.getDistance());

        return ResponseEntity.created(URI.create("/lines/" + lineId))
            .body(LineResponse.of(lineQueryService.findById(lineId)));
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> getLines() {
        List<LineResponse> lines = lineQueryService.findLines();
        return ResponseEntity.ok(lines);
    }

    @GetMapping(value = "/{lineId}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long lineId) {
        return ResponseEntity.ok(LineResponse.of(lineQueryService.findById(lineId)));
    }

    @PutMapping(value = "/{lineId}")
    public ResponseEntity<Void> updateLine(@PathVariable Long lineId,
        @RequestBody LineRequest lineRequest) {
        lineCommandService.update(lineId, lineRequest.toLine());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long lineId) {
        lineCommandService.deleteById(lineId);
        return ResponseEntity.noContent().build();
    }
}
