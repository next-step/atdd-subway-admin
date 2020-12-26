package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
//@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        ResponseEntity responseEntity = ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
        return responseEntity;
    }

    @GetMapping("/lines")
    public ResponseEntity listLine() {
        List<LineResponse> lineResponseList = lineService.listLine();
        ResponseEntity responseEntity = ResponseEntity.ok(lineResponseList);
        return responseEntity;
    }

    @GetMapping(value = "/lines/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        LineResponse lineResponse =  lineService.findLineById(id);
        return ResponseEntity.ok().body(lineResponse);
    }

    @PatchMapping(value = "/lines")
    public ResponseEntity modifyLine(@RequestBody LineRequest lineRequest) {
        lineService.update(lineRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
