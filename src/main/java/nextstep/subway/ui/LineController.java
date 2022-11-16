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

import nextstep.subway.application.line.LineService;
import nextstep.subway.dto.line.LineCreateRequest;
import nextstep.subway.dto.line.LineResponse;
import nextstep.subway.dto.line.LineUpdateRequest;

@RestController
@RequestMapping("/lines")
public class LineController {

	private final LineService lineService;

	public LineController(LineService lineService) {
		this.lineService = lineService;
	}

	@PostMapping
	public ResponseEntity<LineResponse> createLine(@RequestBody LineCreateRequest request) {
		LineResponse response = lineService.saveLine(request);
		return ResponseEntity.created(URI.create("/lines/" + response.getId())).body(response);
	}

	@GetMapping
	public ResponseEntity<List<LineResponse>> getLines() {
		return ResponseEntity.ok().body(lineService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<LineResponse> getLine(@PathVariable(name = "id") Long id) {
		return ResponseEntity.ok().body(lineService.findLine(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> updateLine(@PathVariable(name = "id") Long id, @RequestBody LineUpdateRequest request) {
		lineService.updateLine(id, request.getName(), request.getColor());
		return ResponseEntity.ok(null);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteLine(@PathVariable(name = "id") Long id) {
		lineService.deleteLine(id);
		return ResponseEntity.noContent().build();
	}

}
