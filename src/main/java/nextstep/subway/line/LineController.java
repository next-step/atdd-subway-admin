package nextstep.subway.line;

import java.net.URI;
import java.util.List;
import nextstep.subway.line.dto.LineRequestDto;
import nextstep.subway.line.dto.LineResponseDto;
import nextstep.subway.line.dto.UpdateLineRequestDto;
import nextstep.subway.shared.LineService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @PostMapping(value = "/lines")
    public ResponseEntity<LineResponseDto> createLine(@RequestBody LineRequestDto lineRequestDto) {
        LineResponseDto line = lineService.createLine(lineRequestDto);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponseDto>> showLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }

    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponseDto> showLine(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(lineService.findLine(id));
    }

    @PutMapping(value = "/lines/{id}")
    public ResponseEntity<LineResponseDto> updateLine(@PathVariable("id") Long id,
                                                      @RequestBody UpdateLineRequestDto updateLineRequestDto) {
        lineService.updateLine(id, updateLineRequestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/lines/{id}")
    public ResponseEntity<LineResponseDto> deleteLine(@PathVariable("id") Long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }
}
