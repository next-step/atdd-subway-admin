package nextstep.subway.ui;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PutMapping("/lines/{id}")
    public ResponseEntity updateLine(@PathVariable Long id){
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id){
        return ResponseEntity.badRequest().build();
    }

}
