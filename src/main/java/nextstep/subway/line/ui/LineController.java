package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineQueryService;
import nextstep.subway.line.application.LineCommandService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Distance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineCommandService lineCommandService;
    private final LineQueryService lineQueryService;

    public LineController(LineCommandService lineCommandService, LineQueryService lineQueryService) {
        this.lineCommandService = lineCommandService;
        this.lineQueryService = lineQueryService;
    }

    @PostMapping
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        Long id = lineCommandService.saveLine(lineRequest.toLine(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId(),
                new Distance(lineRequest.getDistance()));

        LineResponse line = LineResponse.of(lineQueryService.findByIdFetched(id));

        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public ResponseEntity getLines() {
        List<Line> lines = lineQueryService.findAllFetched();
        return ResponseEntity.ok(
                lines
                        .stream()
                        .map(LineResponse::of)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity getLine(@PathVariable Long id) {
        Line line = lineQueryService.findByIdFetched(id);
        return ResponseEntity.ok(
                LineResponse.of(line)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity getLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineCommandService.update(id, lineRequest.toLine());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineCommandService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}
