package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Objects;

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
/*        if(Objects.isNull(line)) {
            return new ResponseEntity<>("입력값이 잘못 되었습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }*/
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public List<LineResponse> findAllLines() {
        return lineService.findByAll();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity findLine(@PathVariable Long id) {
        LineResponse line = lineService.findById(id);
        if(Objects.isNull(line.getId())) {
            return new ResponseEntity<>("입력값이 잘못 되었습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok().body(line);

    }

    @PutMapping(path = "/{id}")
    public ResponseEntity modifyLine(@RequestBody LineRequest lineRequest, @PathVariable Long id) {
        LineResponse line = lineService.updateLine(lineRequest, id);
        return ResponseEntity.ok().body(line);
    }


    @DeleteMapping(path = "/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
