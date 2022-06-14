package nextstep.subway.ui;

import nextstep.subway.application.LineService;
import nextstep.subway.dto.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class LineController {

    private LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines" + line.getId())).body(line);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }

    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(lineService.findLine(id));
    }

    @PutMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateLine(@PathVariable Long id
            , @RequestBody LineUpdateRequest lineUpdateRequest) {
        lineService.updateLine(id, lineUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<SectionResponse> addSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        SectionResponse sectionResponse = lineService.addSection(lineId, sectionRequest);
        return ResponseEntity.created(URI.create("lines" + lineId + "/sections")).body(sectionResponse);
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<SectionResponse> removeLineStation(@PathVariable Long lineId,
                                                             @RequestParam Long stationId) {
        lineService.removeSectionByStationId(lineId, stationId);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(value = {NoSuchElementException.class, IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Void> handle() {
        return ResponseEntity.badRequest().build();
    }

}
