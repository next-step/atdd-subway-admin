package nextstep.subway.ui;

import java.net.URI;
import java.util.List;
import nextstep.subway.application.LineService;
import nextstep.subway.dto.LineEditRequest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.http.HttpStatus;
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

    public LineController(LineService lineService){
        this.lineService = lineService;
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> findAllLines(){
        List<LineResponse> lines = lineService.findAllLines();
        return new ResponseEntity<>(lines, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest){
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> findLine(@PathVariable("id") Long id){
        LineResponse line = lineService.findLine(id);
        return new ResponseEntity<>(line, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateLine(@PathVariable("id") Long id,
                                     @RequestBody LineEditRequest lineEditRequest){
        lineService.updateLine(id, lineEditRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable("id") Long id){
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }

}
