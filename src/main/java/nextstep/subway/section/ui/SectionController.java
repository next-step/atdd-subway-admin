package nextstep.subway.section.ui;

import javafx.beans.binding.LongExpression;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineCreateRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionAddRequest;
import nextstep.subway.section.dto.SectionAddResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/lines/{lineId}/sections")
public class SectionController {

    @Autowired
    private LineService lineService;

    @PostMapping(value = "")
    public ResponseEntity<SectionAddResponse> addSectionToLine(
            @PathVariable Long lineId,
            @RequestBody SectionAddRequest sectionAddRequest) {
        SectionAddResponse response = lineService.addSection(lineId, sectionAddRequest);
        return ResponseEntity.created(URI.create("/" + lineId + "/sections/" + response.getId())).body(response);
    }


}
