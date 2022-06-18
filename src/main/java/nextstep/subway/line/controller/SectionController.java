package nextstep.subway.line.controller;

import nextstep.subway.line.dto.LineResponseDto;
import nextstep.subway.line.dto.SectionRequestDto;
import nextstep.subway.line.service.LineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SectionController {
    private final LineService lineService;

    public SectionController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping(value = "/lines/{id}/sections")
    public ResponseEntity<LineResponseDto> createLine(@PathVariable("id") Long id,
                                                      @RequestBody SectionRequestDto sectionRequestDto) {
        return ResponseEntity.ok().body(lineService.addSection(id, sectionRequestDto));
    }

    @DeleteMapping(value = "/lines/{id}/sections")
    public ResponseEntity<LineResponseDto> deleteLine(@PathVariable("id") Long id, @RequestParam("stationId") Long stationId) {
        lineService.deleteStation(id, stationId);
        return ResponseEntity.noContent().build();
    }
}
