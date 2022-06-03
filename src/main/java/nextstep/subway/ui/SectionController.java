package nextstep.subway.ui;

import java.util.List;
import nextstep.subway.application.LineSectionService;
import nextstep.subway.application.SectionService;
import nextstep.subway.dto.request.SectionRequest;
import nextstep.subway.dto.response.SectionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/lines/{lineId}/sections")
public class SectionController {

    private final SectionService sectionService;
    private final LineSectionService lineSectionService;

    public SectionController(SectionService sectionService, LineSectionService lineSectionService) {
        this.sectionService = sectionService;
        this.lineSectionService = lineSectionService;
    }

    @PostMapping()
    public ResponseEntity saveSection(@PathVariable Long lineId,
        @RequestBody SectionRequest sectionRequest) {
        sectionService.createSection(lineId, sectionRequest);
        return ResponseEntity.ok()
            .build();
    }

    @GetMapping("")
    public ResponseEntity<List<SectionResponse>> findAll(@PathVariable Long lineId) {
        List<SectionResponse> sections = sectionService.findAllByLine(lineId);
        return ResponseEntity.ok(sections);
    }

    @DeleteMapping
    public ResponseEntity deleteStation(@PathVariable Long lineId,
        @RequestParam Long stationId) {
        lineSectionService.deleteStation(lineId, stationId);

        return ResponseEntity.ok()
            .build();
    }


}
