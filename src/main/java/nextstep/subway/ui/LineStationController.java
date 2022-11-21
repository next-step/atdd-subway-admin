package nextstep.subway.ui;

import nextstep.subway.application.LineStationService;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lines")
public class LineStationController {
    private final LineStationService lineStationService;

    public LineStationController(LineStationService lineStationService) {
        this.lineStationService = lineStationService;
    }


    @PostMapping("/{id}/sections")
    public ResponseEntity addLineStation(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        lineStationService.addLineStation(id, sectionRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
