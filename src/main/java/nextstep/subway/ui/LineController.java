package nextstep.subway.ui;

import nextstep.subway.application.LineService;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.dto.LineCreateRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.dto.SectionCreateRequest;
import nextstep.subway.dto.SectionCreateResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/lines")
public class LineController {

    private LineService lineService;
    private LineRepository lineRepository;

    public LineController(LineService lineService, LineRepository lineRepository) {
        this.lineService = lineService;
        this.lineRepository = lineRepository;
    }

    /* 노선 생성 */
    @PostMapping
    public ResponseEntity<LineResponse> create(@RequestBody LineCreateRequest request) {
        LineResponse response = lineService.create(request.toLine());
        return ResponseEntity.created(URI.create("lines/" + response.getId())).body(response);
    }

    /* 노선 목록 조회 */
    @GetMapping
    public ResponseEntity<List<LineResponse>> list() {
        return ResponseEntity.ok(lineService.findList());
    }

    /* 노선 단건 조회 */
    @GetMapping(value = "/{id}")
    public ResponseEntity<LineResponse> get(@PathVariable(value = "id") long id) {
        Optional<LineResponse> response = lineService.find(id);
        return response.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /* 노선 단건 수정 */
    @PutMapping(value = "/{id}")
    public ResponseEntity update(@PathVariable(value = "id") long id,
            @RequestBody LineUpdateRequest request) {
        lineService.update(id, request);
        return ResponseEntity.ok().build();
    }

    /* 노선 삭제 */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity delete(@PathVariable(value = "id") long id) {
        lineRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /* 노선 구간 생성 */
    @PostMapping(value = "/{id}/sections")
    public ResponseEntity<SectionCreateResponse> createSection(@PathVariable(value = "id") long id,
            @RequestBody SectionCreateRequest request) {
        SectionCreateResponse response = lineService.createSection(id, request);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
    public ResponseEntity handleIllegalArgsException() {
        return ResponseEntity.badRequest().build();
    }
}
