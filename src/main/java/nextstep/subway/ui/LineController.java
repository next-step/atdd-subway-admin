package nextstep.subway.ui;

import java.net.URI;
import java.util.List;
import nextstep.subway.application.LineService;
import nextstep.subway.domain.Line;
import nextstep.subway.dto.LineRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping(value = "/lines", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Line> createLine(@RequestBody LineRequest lineRequest){
        Line newLine = lineService.saveLine(lineRequest);
        return ResponseEntity
                .created(URI.create("/lines/"+ newLine.getId()))
                .body(newLine);
    }

    @GetMapping(value = "/lines", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Line>> getLines(){
        List<Line> lines = lineService.getLines();
        return ResponseEntity.ok(lines);
    }
}
