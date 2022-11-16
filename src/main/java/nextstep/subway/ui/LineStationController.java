package nextstep.subway.ui;

import nextstep.subway.application.LineService;
import nextstep.subway.application.LineStationService;
import nextstep.subway.domain.LineStation;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LineStationController {

    private final LineStationService lineStationService;
    private final LineService lineService;

    public LineStationController(LineStationService lineStationService, LineService lineService) {
        this.lineStationService = lineStationService;
        this.lineService = lineService;
    }

    @PostMapping("/lines/{id}/sections")
    public ResponseEntity addSection(@PathVariable("id") Long id,
                                     @RequestBody SectionRequest sectionRequest){
        lineStationService.addSection(id, sectionRequest);
        LineResponse line = lineService.findLine(id);
        return ResponseEntity.ok(line);
    }
}
