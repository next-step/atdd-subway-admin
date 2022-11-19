package nextstep.subway.ui;

import nextstep.subway.application.LineQueryService;
import nextstep.subway.application.LineService;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {

	private final LineService lineService;
	private final LineQueryService lineQueryService;

	public LineController(LineService lineService, LineQueryService lineQueryService) {
		this.lineService = lineService;
		this.lineQueryService = lineQueryService;
	}

	@PostMapping
	public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
		LineResponse lineResponse = lineService.saveLine(lineRequest);

		return ResponseEntity.created(createLinesLocation(lineResponse.getId()))
				.body(lineResponse);
	}

	@GetMapping
	public ResponseEntity<List<LineResponse>> getLines() {
		List<LineResponse> lineResponse = lineQueryService.findAllLines();
		return ResponseEntity.ok().body(lineResponse);
	}

	@GetMapping("/{id}")
	public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
		LineResponse lineResponse = lineQueryService.findLine(id);
		return ResponseEntity.ok().body(lineResponse);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
		lineService.updateLine(id, lineRequest);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> removeLine(@PathVariable Long id) {
		lineService.removeLine(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{id}/sections")
	public ResponseEntity<LineResponse> createSection(@PathVariable Long id,
													  @RequestBody SectionRequest request) {
		LineResponse lineResponse = lineService.addSections(id, request);
		return ResponseEntity.created(createLinesLocation(id)).body(lineResponse);
	}

	@DeleteMapping("/{id}/sections")
	public ResponseEntity<Void> removeSection(@PathVariable Long id,
											  @RequestParam Long stationId) {
		lineService.removeSection(id, stationId);
		return ResponseEntity.noContent().build();
	}

	private URI createLinesLocation(long lineId) {
		return URI.create("/lines/" + lineId);
	}
}
