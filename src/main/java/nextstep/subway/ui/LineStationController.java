package nextstep.subway.ui;

import nextstep.subway.application.LineStationService;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LineStationController {

    private final LineStationService lineStationService;

    public LineStationController(LineStationService lineStationService) {
        this.lineStationService = lineStationService;
    }

    @PostMapping("/lines/{id}/sections")
    public ResponseEntity addSection(@PathVariable("id") Long id,
                                     @RequestBody SectionRequest sectionRequest){
        lineStationService.addSection(id, sectionRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}/sections")
    public ResponseEntity deleteSection(@PathVariable("id") Long lineId,
                                        @RequestParam("stationId") Long stationId){
        lineStationService.deleteSection(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
