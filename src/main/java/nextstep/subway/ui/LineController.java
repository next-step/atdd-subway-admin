package nextstep.subway.ui;

import java.net.URI;
import java.util.List;
import nextstep.subway.application.LineService;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("line")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        final LineResponse lineResponse = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping("lines")
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAllLine());
    }

    @GetMapping("lines/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable("id") Long lineId) {
        return ResponseEntity.ok().body(lineService.findLine(lineId));
    }

    @PutMapping("lines/{id}")
    public ResponseEntity fixLine(@PathVariable("id") Long lineId, @RequestBody LineRequest request) {
        lineService.updateLine(lineId, request);
        return ResponseEntity.ok().build();
    }

}
