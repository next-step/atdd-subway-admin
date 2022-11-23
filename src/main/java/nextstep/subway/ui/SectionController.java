package nextstep.subway.ui;

import nextstep.subway.application.SectionService;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class SectionController {

    private SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> addSection(@PathVariable("lineId") Long lineId, @RequestBody SectionRequest request) {
        sectionService.createSection(lineId, request);
        return ResponseEntity.created(URI.create("/lines/" + lineId + "/sections")).build();
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> deleteStationInSection(@PathVariable("lineId") Long lineId, @RequestParam("stationId") Long stationId) {
        sectionService.deleteStation(lineId, stationId);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity IllegalArgumentException() {return ResponseEntity.badRequest().build();}

}
