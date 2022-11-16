package nextstep.subway.ui;

import nextstep.subway.application.LineService;
import nextstep.subway.domain.Line;
import nextstep.subway.dto.CreateLineDto;
import nextstep.subway.dto.LineResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody CreateLineDto dto) {
        Line registeredLine = lineService.register(dto);
        LineResponse response = LineResponse.of(registeredLine);
        return ResponseEntity.created(URI.create("/lines/" + response.getId())).body(response);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }

    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(lineService.findLine(id));
    }
}
