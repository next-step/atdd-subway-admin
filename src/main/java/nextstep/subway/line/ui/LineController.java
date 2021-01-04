package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {
    private LineService lineService;

    private StationService stationService;

    public LineController(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @PostMapping("/lines")
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> listLine() {
        return ResponseEntity.ok().body(lineService.listLine());
    }

    @GetMapping(value = "/lines/{id}")
    public ResponseEntity<LineResponse> detailLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(lineService.findLineById(id));
    }

    @PutMapping(value = "/lines")
    public ResponseEntity modifyLine(@RequestBody LineRequest lineRequest) {
        lineService.update(lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }

//    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity addSection(
            @PathVariable Long lineId,
            @RequestBody SectionRequest sectionRequest) {
//        sectionService.addSection(lineId, sectionRequest);
        return ResponseEntity.created(URI.create("/lines/" + String.valueOf(lineId))).build();
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity new_addSection(
            @PathVariable Long lineId,
            @RequestBody SectionRequest sectionRequest) {
        lineService.new_addSection(lineId, sectionRequest);
        return ResponseEntity.created(URI.create("/lines/" + String.valueOf(lineId))).build();
    }

}
