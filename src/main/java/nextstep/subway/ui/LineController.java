package nextstep.subway.ui;

import nextstep.subway.application.LineService;
import nextstep.subway.dto.LineCreateRequest;
import nextstep.subway.dto.LineCreateResponse;
import nextstep.subway.dto.LineListResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;

@RestController
@RequestMapping("line")
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineCreateResponse> createLine(@RequestBody LineCreateRequest lineCreateRequest) {
        LineCreateResponse lineCreateResponse = lineService.saveLine(lineCreateRequest);
        return ResponseEntity.created(URI.create("/")).body(lineCreateResponse);
    }

    @GetMapping
    public ResponseEntity<LineListResponse> getLines() {
        LineListResponse lineListResponse = new LineListResponse(new ArrayList<>());
        return ResponseEntity.ok(lineListResponse);
    }
}
