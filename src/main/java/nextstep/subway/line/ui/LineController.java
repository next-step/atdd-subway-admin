package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

import javax.sound.sampled.Line;

@RestController
@RequestMapping("/lines")
public class LineController {
	private final LineService lineService;

	public LineController(final LineService lineService) {
		this.lineService = lineService;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LineResponse>> showLines() {
		return ResponseEntity.ok(lineService.findAllLines());
	}

	@GetMapping(value = "/{lineId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LineResponse> showLine(@PathVariable("lineId") Long id) {
		return ResponseEntity.ok(lineService.findById(id));
	}

	@PostMapping
	public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
		LineResponse line = lineService.saveLine(lineRequest);
		return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
	}

	@PostMapping("/{lineId}/sections")
	public ResponseEntity<LineResponse> addSection(@PathVariable("lineId") Long lineId,
		@RequestBody SectionRequest sectionRequest) {
		LineResponse line = lineService.addSection(lineId, sectionRequest);
		return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
	}

	@PatchMapping(value = "/{lineId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LineResponse> updateLine(@PathVariable("lineId") Long id,
		@RequestBody LineRequest lineRequest) {
		return ResponseEntity.ok(lineService.updateById(id, lineRequest));
	}

	@DeleteMapping(value = "/{lineId}")
	public ResponseEntity<Void> deleteLine(@PathVariable("lineId") Long id) {
		lineService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping(value = "/{lineId}/sections")
	public ResponseEntity<Void> deleteLine(@PathVariable("lineId") Long lineId, @RequestParam("stationId") Long stationId) {
		lineService.deleteStationByIdInLine(lineId, stationId);
		return ResponseEntity.noContent().build();
	}

}
