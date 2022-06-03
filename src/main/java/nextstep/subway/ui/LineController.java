package nextstep.subway.ui;

import static java.net.URI.create;

import java.util.List;
import nextstep.subway.application.LineService;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity
                .ok(lineService.findAllLines());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        return ResponseEntity
                .ok(lineService.findLine(id));
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse response = lineService.saveLine(lineRequest);
        return ResponseEntity
                .created(create("/lines/" + response.getId()))
                .body(response);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity changeLineInformation(@PathVariable Long id,
                                                @RequestBody LineRequest lineRequest) {
        lineService.updateLine(id, lineRequest);
        return ResponseEntity
                .ok()
                .build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity removeLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping(value = "/{id}/sections")
    public ResponseEntity createSection(@PathVariable Long id,
                                        @RequestBody SectionRequest sectionRequest) {
        lineService.addSection(id, sectionRequest);
        return ResponseEntity
                .ok()
                .build();
    }

    @DeleteMapping(value = "/{id}/sections")
    public ResponseEntity deleteSection(@PathVariable Long id,
                                        @RequestParam("stationId") Long stationId) {
        lineService.deleteSection(id, stationId);
        return ResponseEntity
                .noContent()
                .build();
    }
}
