package nextstep.subway.ui;

import nextstep.subway.dto.NewLineRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LineController {

    @PostMapping
    public ResponseEntity<NewLineRequest> createLine(@RequestBody NewLineRequest request) {
        return ResponseEntity.created(null).body(null);
    }
}
