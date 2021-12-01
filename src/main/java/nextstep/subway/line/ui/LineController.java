package nextstep.subway.line.ui;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.save(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }
    
    @GetMapping
    public ResponseEntity<List<LineResponse>> findLines() {
        return ResponseEntity.ok(lineService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> findLineById(@PathVariable Long id) {
        return ResponseEntity.ok(lineService.find(id));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest request) {
        lineService.update(id, request);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/sections")
    public ResponseEntity<LineResponse> addSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        lineService.addSection(id, sectionRequest);
        return ResponseEntity.ok().build();
    }

}
