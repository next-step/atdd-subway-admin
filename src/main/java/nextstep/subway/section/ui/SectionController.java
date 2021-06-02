package nextstep.subway.section.ui;

import nextstep.subway.section.application.SectionCommandService;
import nextstep.subway.section.application.SectionQueryService;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.stream.Collectors;

import static java.lang.String.format;

@RestController
@RequestMapping("/{lineId}/sections")
public class SectionController {
    private final SectionCommandService sectionCommandService;
    private final SectionQueryService sectionQueryService;

    public SectionController(SectionCommandService sectionCommandService, SectionQueryService sectionQueryService) {
        this.sectionCommandService = sectionCommandService;
        this.sectionQueryService = sectionQueryService;
    }

    @PostMapping
    public ResponseEntity createSection(@RequestBody SectionRequest sectionRequest, @PathVariable Long lineId) {
        Long id = sectionCommandService.save(lineId,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance()
        );

        return ResponseEntity.created(URI.create(format("/%d/sections/%d", lineId, id)))
                .body(SectionResponse.of(sectionQueryService.findByIdFetched(id)));
    }

    @GetMapping
    public ResponseEntity list(@PathVariable Long lineId) {
        return ResponseEntity.ok(
                sectionQueryService.findAllByLineIdFetchedOrderByUpToDownStation(lineId)
                .mapOrderByUpStationToDownStation(SectionResponse::of)
                .collect(Collectors.toList())
        );
    }
}
