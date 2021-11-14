package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineInfoResponse;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public ResponseEntity findLineInfos() {
        List<LineInfoResponse> lines = lineService.findAllForLineInfo();
        return ResponseEntity.ok().body(lines);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity findLineInfo(@PathVariable Long id) {
        LineInfoResponse lines = lineService.findLineInfo(id);
        return ResponseEntity.ok().body(lines);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateLineInfo(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.updateLineInfo(id, new Line(lineRequest.getName(), lineRequest.getColor()));
        return ResponseEntity.ok().build();
    }
}
