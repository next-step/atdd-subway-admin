package nextstep.subway.ui;

import nextstep.subway.application.LineService;
import nextstep.subway.dto.NewLineRequest;
import nextstep.subway.dto.NewLineResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<NewLineResponse> createLine(@RequestBody NewLineRequest request) throws URISyntaxException {
        NewLineResponse response = lineService.saveLine(request);
        return ResponseEntity.created(new URI("/lines/" + response.getId())).body(response);
    }
}
