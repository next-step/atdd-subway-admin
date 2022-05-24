package nextstep.subway.ui;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LineController {

    @PostMapping("/lines")
    public ResponseEntity createLines(){
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/lines")
    public ResponseEntity showLines(){
        return ResponseEntity.badRequest().build();
    }

}
