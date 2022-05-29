package nextstep.subway.ui;

import javassist.NotFoundException;
import nextstep.subway.application.LineService;
import nextstep.subway.domain.Line;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {
    private LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> findAllLines() {
        return ResponseEntity.ok().body(
                lineService.findAllLines()
                        .stream()
                        .map(LineResponse::of)
                        .collect(Collectors.toList())
        );
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest.Create lineCreateRequest) throws NotFoundException {
        Line createResult = lineService.saveLine(lineCreateRequest);
        return ResponseEntity
                .created(URI.create("/lines/" + createResult.getId()))
                .body(LineResponse.of(createResult));
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> findLine(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok()
                .body(LineResponse.of(lineService.findById(id)));
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity modifyLine(@PathVariable Long id, @RequestBody LineRequest.Modification modify) throws NotFoundException {
        lineService.modifyLine(id, modify);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }
}
