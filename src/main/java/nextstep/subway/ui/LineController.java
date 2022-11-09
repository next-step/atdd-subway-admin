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
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.application.LineService;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;

@RestController
public class LineController {

	private final LineService lineService;

	public LineController(LineService lineService) {
		this.lineService = lineService;
	}

	@PostMapping("/lines")
	public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
		LineResponse lineResponse = lineService.saveLine(lineRequest);

		return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
	}

	@GetMapping("/lines")
	public ResponseEntity<List<LineResponse>> getLines() {
		List<LineResponse> lineResponse = lineService.findAllLines();
		return ResponseEntity.ok().body(lineResponse);
	}

	@GetMapping("/lines/{id}")
	public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
		LineResponse lineResponse = lineService.findLine(id);
		return ResponseEntity.ok().body(lineResponse);
	}

	@PutMapping("/lines/{id}")
	public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
		lineService.updateLine(id, lineRequest);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/lines/{id}")
	public ResponseEntity<Void> removeLine(@PathVariable Long id) {
		lineService.removeLine(id);
		return ResponseEntity.noContent().build();
	}

}
