package nextstep.subway.common.ui;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 프론트엔드 페이지를 응답하기 위한 컨트롤러 입니다.
 * 프론트엔드 페이지를 변경할 경우가 아니면 미션 진행 간에는 수정할 필요가 없습니다.
 */
@Controller
public class PageController {
    @GetMapping(value = {"/", "/stations", "/lines", "/sections"}, produces = MediaType.TEXT_HTML_VALUE)
    public String index() {
        return "index";
    }
}
