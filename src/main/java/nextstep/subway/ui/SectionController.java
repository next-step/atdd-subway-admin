package nextstep.subway.ui;

import nextstep.subway.application.LineService;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineStationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static java.lang.String.format;

@RequestMapping("/lines")
@RestController
public class SectionController {

    private final LineService lineService;

    public SectionController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<LineResponse> createSection(@PathVariable long id,
                                                      @RequestBody LineStationRequest request) {
        LineResponse lineResponse = lineService.addSections(id, request);
        return ResponseEntity.created(createLocation(id)).body(lineResponse);
    }

    private URI createLocation(long lineId) {
        return URI.create(format("/lines/%s", lineId));
    }
}
