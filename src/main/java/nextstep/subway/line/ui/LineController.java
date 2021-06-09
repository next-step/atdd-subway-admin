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

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
		return ResponseEntity.ok(lineService.getLine(id));
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<LineResponse> updateLine(@PathVariable Long id,
												   @RequestBody LineRequest lineRequest) {
		lineService.updateLine(id, lineRequest);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<LineResponse> deleteLine(@PathVariable Long id) {
		lineService.deleteLine(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{id}/sections")
	public ResponseEntity<LineResponse> registerSection(@PathVariable Long id,
														@RequestBody SectionRequest request) {
		LineResponse line = lineService.registerSection(id, request);
		return ResponseEntity.ok(line);
	}
}
