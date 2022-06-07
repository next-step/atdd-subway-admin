package nextstep.subway.ui;

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

import nextstep.subway.application.LineService;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;

@RestController
@RequestMapping("/lines")
public class LineController {
	private LineService lineService;

	public LineController(LineService lineService) {
		this.lineService = lineService;
	}

	@PostMapping
	public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
		LineResponse lineResponse = lineService.saveLine(lineRequest);
		return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
	}

	@GetMapping
	public ResponseEntity<List<LineResponse>> getLines() {
		return ResponseEntity.ok().body(lineService.findAllLines());
	}

	@GetMapping("/{id}")
	public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
		return ResponseEntity.ok().body(lineService.findById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
		lineService.updateLine(id, lineRequest);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity deleteLine(@PathVariable Long id) {
		lineService.deleteLine(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{lineId}/sections")
	public ResponseEntity createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
		lineService.addSection(lineId, sectionRequest);
		return ResponseEntity.ok().build();
	}
}
