package nextstep.subway.ui;

import java.net.URI;
import java.util.List;
import nextstep.subway.application.LineService;
import nextstep.subway.dto.LineCreateRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.dto.SectionRequest;
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

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping()
    public ResponseEntity<List<LineResponse>> getAllLines() {
        List<LineResponse> lineResponseList = lineService.getAllLines();
        return ResponseEntity.ok(lineResponseList);
    }

    @PostMapping()
    public ResponseEntity<LineResponse> createLine(@RequestBody LineCreateRequest lineCreateRequest) {
        LineResponse line = lineService.saveLine(lineCreateRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> findById(
        @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(lineService.getLineById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> updateLine(
        @PathVariable("id") Long id, @RequestBody LineUpdateRequest lineuUpdateRequest
    ) {
        return ResponseEntity.ok(lineService.updateLine(id, lineuUpdateRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(
        @PathVariable("id") Long id
    ) {
        lineService.deleteLine(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<LineResponse> updateSection(
        @PathVariable final Long id,
        @RequestBody final SectionRequest sectionRequest
    ) {
        return ResponseEntity.ok(lineService.updateSection(id, sectionRequest));
    }

}
