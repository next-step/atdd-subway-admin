package nextstep.subway.ui;

import nextstep.subway.application.LineBridgeService;
import nextstep.subway.dto.LineBridgeDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class LineBridgeController {

    private final LineBridgeService lineBridgeService;

    public LineBridgeController(LineBridgeService lineBridgeService) {
        this.lineBridgeService = lineBridgeService;
    }

    @PostMapping("/lines/{id}/bridges")
    public ResponseEntity<LineBridgeDto.Response> createLineBridge(@PathVariable Long id,
                                                         @RequestBody LineBridgeDto.Request lineBridgeRequest) {
        LineBridgeDto.Response lineBridgeResponse = lineBridgeService.createLineBridge(id, lineBridgeRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineBridgeResponse.getId())).body(lineBridgeResponse);
    }

}
