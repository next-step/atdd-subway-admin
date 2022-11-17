package nextstep.subway.linebridge.ui;

import javax.websocket.server.PathParam;
import nextstep.subway.linebridge.dto.LineBridgeDto;
import nextstep.subway.linebridge.repotitory.LineBridgeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @PostMapping("/lines/{id}/sections")
    public ResponseEntity<LineBridgeDto.Response> createLineBridge(@PathVariable Long id,
                                                                   @RequestBody LineBridgeDto.Request lineBridgeRequest) {
        LineBridgeDto.Response lineBridgeResponse = lineBridgeService.createLineBridge(id, lineBridgeRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineBridgeResponse.getId())).body(lineBridgeResponse);
    }

    @DeleteMapping("/lines/{id}/sections")
    public ResponseEntity<LineBridgeDto.Response> deleteLineBridge(@PathVariable Long id, @PathParam("stationId") Long stationId) {
        lineBridgeService.deleteLineBridgeByStationId(id, stationId);
        return ResponseEntity.ok().build();
    }

}
