package nextstep.subway.line.ui;

import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    /**
     * 단일 조회
     *
     * @param id
     * @return
     * @throws NotFoundException
     */
    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable Long id) throws NotFoundException {
        LineResponse line = lineService.findOne(id);
        return ResponseEntity.ok(line);
    }

    /**
     * 전체목록 조회
     *
     * @return
     */
    @GetMapping
    public ResponseEntity findAll() {
        LinesResponse lines = lineService.findAll();
        return ResponseEntity.ok(lines);
    }

    /**
     * 노선 생성
     *
     * @param lineRequest
     * @return
     */
    @PostMapping
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    /**
     * 노선 업데이트
     *
     * @param id
     * @param lineRequest
     * @return
     * @throws NotFoundException
     */
    @PutMapping("/{id}")
    public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) throws NotFoundException {
        LineResponse line = lineService.updateLine(id, lineRequest);
        return ResponseEntity.ok(line);
    }

    /**
     * 노선 삭제
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }

}
