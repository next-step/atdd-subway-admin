package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

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

    /**
     * 모든 지하철 노선을 반환합니다.
     * @return 저장된 모든 지하철 노선
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> findAllLines() {
        return ResponseEntity.ok().body(this.lineService.findAllLines());
    }

    /**
     * ID로 지하철 노선을 검색합니다
     * @param id 
     * @return 검색 된 지하철 노선
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> findLine(@PathVariable Long id) {
        return this.lineService.findLine(id)
                    .map(lineResponse -> ResponseEntity.ok().body(lineResponse))
                        .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * 주어진 ID의 노선이 있는 경우 내용을 수정합니다.
     * @param id
     * @param lineRequest
     * @return HttpStatus.OK
     */
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        this.notExistLineById(id);

        this.lineService.saveLine(lineRequest);
        return ResponseEntity.ok().build();
    }

    /**
     * 해당 ID의 노선이 있으면 삭제합니다.
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> deleteLine(@PathVariable Long id) {
        this.notExistLineById(id);

        this.lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 해당 ID로 노선이 존재하지 않으면 예외를 발생시킵니다.
     * @param id
     */
    private void notExistLineById(Long id) {
        this.lineService.findLine(id)
                .orElseThrow(() -> new IllegalArgumentException("cannot find line."));
    }

}
