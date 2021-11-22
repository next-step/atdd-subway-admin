package nextstep.subway.line.ui;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;

@RestController
@RequestMapping("/lines")
public class LineController {
	private final LineService lineService;

	public LineController(final LineService lineService) {
		this.lineService = lineService;
	}

	@PostMapping
	public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
		LineResponse line = lineService.saveLine(lineRequest);
		return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
	}

	@GetMapping
	public ResponseEntity<List<LineResponse>> findAll() {
		List<LineResponse> lineResponseList = lineService.findAll();
		return ResponseEntity.ok(lineResponseList);
	}

	@GetMapping("/{id}")
	public ResponseEntity<LineResponse> findById(
		@PathVariable("id") Long id
	) {
		return ResponseEntity.ok(lineService.findById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<LineResponse> updateLine(
		@PathVariable("id") Long id, @RequestBody LineRequest lineRequest
	) {
		return ResponseEntity.ok(lineService.updateLine(id, lineRequest));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteLine(
		@PathVariable("id") Long id
	) {
		lineService.deleteLine(id);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{id}/sections")
	public ResponseEntity<LineResponse> addSection(
		@PathVariable final Long id,
		@RequestBody final SectionRequest sectionRequest
	) {
		return ResponseEntity.ok(lineService.addSection(id, sectionRequest));
	}
}
