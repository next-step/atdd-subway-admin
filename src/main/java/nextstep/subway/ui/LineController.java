package nextstep.subway.ui;

import nextstep.subway.application.LineService;
import nextstep.subway.application.SectionService;
import nextstep.subway.application.StationService;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {

    private final LineService lineService;

    private final StationService stationService;

    private final SectionService sectionService;

    public LineController(LineService lineService, StationService stationService, SectionService sectionService) {
        this.lineService = lineService;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        Station upStation = stationService.fetch(lineRequest.getUpStationId());
        Station downStation = stationService.fetch(lineRequest.getDownStationId());
        long distance = lineRequest.getDistance();
        Section section = sectionService.generate(upStation, downStation, distance);
        LineResponse response = lineService.register(line, section);
        return ResponseEntity.created(URI.create("/lines/" + response.getId())).body(response);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }
}
