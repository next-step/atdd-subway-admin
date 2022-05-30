package nextstep.subway.ui;

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
    @PostMapping
    ResponseEntity<LineCreateResponse> createLine(@RequestBody LineCreateRequest lineCreateRequest) {
        LineCreateResponse lineCreateResponse = new LineCreateResponse(1L);
        return ResponseEntity.created(URI.create("/")).body(lineCreateResponse);
    }

    @GetMapping
    ResponseEntity<LineListResponse> getLines() {
        LineListResponse lineListResponse = new LineListResponse(new ArrayList<>());
        return ResponseEntity.ok(lineListResponse);
    }
}
