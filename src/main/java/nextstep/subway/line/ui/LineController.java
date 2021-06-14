package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {

	private final LineService lineService;

	public LineController(final LineService lineService) {
		this.lineService = lineService;
	}

	@PostMapping()
	public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
		LineResponse line = lineService.saveLine(lineRequest);
		return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LineResponse>> getLines() {
		return ResponseEntity.ok(lineService.getLineRepository());
	}

	@GetMapping(value = "/{lineId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LineResponse> getLine(@PathVariable Long lineId) {
		return ResponseEntity.ok(lineService.getLine(lineId));
	}

	@PutMapping(value = "/{lineId}")
	public ResponseEntity<LineResponse> updateLine(@PathVariable Long lineId,
												   @RequestBody LineRequest lineRequest) {
		LineResponse line = lineService.updateLine(lineId, lineRequest);
		return ResponseEntity.ok(line);
	}

	@DeleteMapping("/{lineId}")
	public ResponseEntity<Void> deleteLine(@PathVariable Long lineId) {
		lineService.deleteLine(lineId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{lineId}/sections")
	public ResponseEntity<LineResponse> registerSection(@PathVariable Long lineId,
														@RequestBody SectionRequest request) {
		LineResponse line = lineService.registerSection(lineId, request);
		return ResponseEntity.ok(line);
	}

	@DeleteMapping("/{lineId}/sections")
	public ResponseEntity<LineResponse> removeSection(@PathVariable Long lineId,
													  @RequestParam Long stationId) {
		LineResponse line = lineService.removeSectionByStationId(lineId, stationId);
		return ResponseEntity.ok(line);
	}
}
