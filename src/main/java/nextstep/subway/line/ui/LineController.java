package nextstep.subway.line.ui;

import lombok.extern.slf4j.Slf4j;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

import java.net.URI;
import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static nextstep.subway.line.dto.LineRequest.*;
import static org.springframework.http.MediaType.*;

@RestController
@RequestMapping("/lines")
@Slf4j
public class LineController {
    private final LineService lineService;
    private final StationRepository stationRepository;

    public LineController(final LineService lineService, final StationRepository stationRepository) {
        this.lineService = lineService;
        this.stationRepository = stationRepository;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId())
                .orElseThrow(() -> new NoResultException("상행 종점역이 존재하지 않습니다"));
        Station downStation = stationRepository.findById(lineRequest.getDownStationId())
                .orElseThrow(() -> new NoResultException("하행 종점역이 존재하지 않습니다"));
        LineStation upLineStation = LineStation.createLineStation(upStation, lineRequest.getDistance());
        LineStation downLineStation = LineStation.createLineStation(downStation, DISTANCE_OF_LAST_STATION);

        LineResponse line = lineService.saveLine(lineRequest, upLineStation, downLineStation);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok(lineService.findAllLines());
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable("id") Long id) {
        return ResponseEntity.ok(lineService.findLineById(id));
    }

    @PutMapping(value = "/{id}", produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<String> updateLine(@PathVariable("id") Long id, @RequestBody LineRequest lineRequest) {
        lineService.updateLine(id, lineRequest);
        return ResponseEntity.ok(lineRequest.getName() + "으로 수정 완료");
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> removeLine(@PathVariable("id") Long id) {
        lineService.removeLine(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Void> handleIllegalArgsException(DataIntegrityViolationException e) {
        log.info("log >>> " + e.getMessage());
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<Void> handleNoResultException(NoResultException e) {
        log.info("log >>> " + e.getMessage());
        return ResponseEntity.notFound().build();
    }
}
