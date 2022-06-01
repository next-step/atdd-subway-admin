package nextstep.subway.ui;


import java.net.URI;
import java.util.List;
import nextstep.subway.application.LineService;
import nextstep.subway.application.StationService;
import nextstep.subway.domain.station.Station;
import nextstep.subway.dto.line.LineRequestDTO;
import nextstep.subway.dto.line.LineResponseDTO;
import nextstep.subway.dto.line.LineResponsesDTO;
import nextstep.subway.dto.line.SectionRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/lines")
@RestController
public class LineController {

    private final LineService lineService;
    private final StationService stationService;

    public LineController(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<LineResponseDTO> createLines(@RequestBody LineRequestDTO lineRequestDTO) {
        Station upStation = stationService.findStation(lineRequestDTO.getUpStationId());
        Station downStation = stationService.findStation(lineRequestDTO.getDownStationId());
        LineResponseDTO lineResponseDTO = lineService.saveLine(upStation, downStation, lineRequestDTO);
        return ResponseEntity.created(URI.create("/lines/" + lineResponseDTO.getId())).body(lineResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<LineResponseDTO>> showLines() {
        LineResponsesDTO lineResponsesDTO = lineService.findAll();
        return ResponseEntity.ok(lineResponsesDTO.getLineResponses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponseDTO> showLine(@PathVariable Long id) {
        LineResponseDTO lineResponsesDTO = lineService.findOne(id);
        return ResponseEntity.ok(lineResponsesDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequestDTO lineRequestDTO) {
        lineService.updateLineInfo(id, lineRequestDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<Void> addSection(@PathVariable Long id, @RequestBody SectionRequestDTO sectionRequestDTO) {
        Station upStation = stationService.findStation(sectionRequestDTO.getUpStationId());
        Station downStation = stationService.findStation(sectionRequestDTO.getDownStationId());
        lineService.addSection(id, upStation, downStation, sectionRequestDTO);
        return ResponseEntity.created(URI.create("/lines/" + id)).build();
    }


    @DeleteMapping("/{id}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable Long id, @RequestParam Long stationId) {
        Station station = stationService.findStation(stationId);
        lineService.deleteSection(id, station);
        return ResponseEntity.ok().build();
    }
}
