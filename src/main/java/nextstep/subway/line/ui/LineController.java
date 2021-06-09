package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineCommandUseCase;
import nextstep.subway.line.application.LineQueryUseCase;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineQueryUseCase lineQueryUseCase;
    private final LineCommandUseCase lineCommandUseCase;

    public LineController(LineQueryUseCase lineQueryUseCase, LineCommandUseCase lineCommandUseCase) {
        this.lineQueryUseCase = lineQueryUseCase;
        this.lineCommandUseCase = lineCommandUseCase;
    }

    /**
     * 지하철 노선 생성
     */
    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineCommandUseCase.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    /**
     * 지하철 노선 목록조회
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> getLines() {
        return ResponseEntity.ok().body(lineQueryUseCase.findAllLines());
    }

    /**
     * 지하철 노선 조회
     */
    @GetMapping(value = "/{id:\\d+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(lineQueryUseCase.findLine(id));
    }

    /**
     * 지하철 노선 수정
     */
    @PutMapping(value = "/{id:\\d+}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineCommandUseCase.updateLine(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    /**
     * 지하철 노선 삭제
     */
    @DeleteMapping(value = "/{id:\\d+}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineCommandUseCase.deleteLine(id);
        return ResponseEntity.noContent().build();
    }
}
