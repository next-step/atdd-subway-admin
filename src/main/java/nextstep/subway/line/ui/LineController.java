package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineQueryService;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;
    private final LineQueryService lineQueryService;

    public LineController(LineService lineService, LineQueryService lineQueryService) {
        this.lineService = lineService;
        this.lineQueryService = lineQueryService;
    }

    @PostMapping
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        Long id = lineService.saveLine(lineRequest.toLine(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId());

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
        lineService.update(id, lineRequest.toLine());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}
