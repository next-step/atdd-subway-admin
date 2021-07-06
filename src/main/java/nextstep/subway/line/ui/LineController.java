package nextstep.subway.line.ui;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;

@RestController
@RequestMapping("/lines")
public class LineController {
	private final LineService lineService;

	public LineController(final LineService lineService) {
		this.lineService = lineService;
	}

	@PostMapping
	public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
		LineResponse line = lineService.saveLine(lineRequest);

		return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
	}

	@GetMapping
	public ResponseEntity getLines() {
		List<LineResponse> lineResponses = lineService.findAllLines();

		return ResponseEntity.ok().body(lineResponses);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity getLine(@PathVariable("id") Long id) {
		LineResponse line = lineService.findById(id);

		return ResponseEntity.ok().body(line);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity updateLine(@PathVariable("id") Long id, @RequestBody LineRequest request) {
		lineService.updateLine(id, request);

		return ResponseEntity.ok().build();
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity deleteLine(@PathVariable("id") Long id) {
		lineService.deleteLine(id);

		return ResponseEntity.ok().build();
	}

	@PostMapping("/{lineId}/sections")
	public ResponseEntity addSection(@PathVariable("lineId") Long lineId, @RequestBody SectionRequest sectionRequest) {
		return ResponseEntity.status(HttpStatus.CREATED).body(lineService.addNewSection(lineId, sectionRequest));
	}

	@DeleteMapping("/{lineId}/sections")
	public ResponseEntity removeLineStation(@PathVariable Long lineId, @RequestParam Long stationId){
		lineService.removeSectionByStationId(lineId, stationId);
		return ResponseEntity.ok().build();
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity handleException(IllegalArgumentException e) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
	}
}
