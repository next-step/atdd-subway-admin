package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineInfoResponse;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;
    private final StationService stationService;

    public LineController(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line savingLine = lineRequest.toLine();
        Section section = null;

        try {
            Station upStation = stationService.findStationById(lineRequest.getUpStationId());
            Station downStation = stationService.findStationById(lineRequest.getDownStationId());

            section = Section.valueOf(upStation, downStation, Distance.valueOf(lineRequest.getDistance()));
        } catch(NoSuchElementException ex) {
        }

        LineResponse line = LineResponse.of(lineService.saveLine(savingLine, section));
        
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public ResponseEntity<List<LineInfoResponse>> findLineInfos() {
        List<LineInfoResponse> lines = lineService.findAllForLineInfo();
        return ResponseEntity.ok().body(lines);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineInfoResponse> findLineInfo(@PathVariable Long id) {
        LineInfoResponse lines = lineService.findLineInfo(id);
        return ResponseEntity.ok().body(lines);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLineInfo(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.updateLineInfo(id, new Line(lineRequest.getName(), lineRequest.getColor()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLineInfo(@PathVariable Long id) {
        lineService.deleteLineInfo(id);
        return ResponseEntity.noContent().build();
    }
}
