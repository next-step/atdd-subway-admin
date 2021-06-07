package nextstep.subway;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping(value = {"/", URIMapping.STATION, URIMapping.LINE, URIMapping.SECTION}, produces = MediaType.TEXT_HTML_VALUE)
    public String index() {
        return "index";
    }

    public static final class URIMapping {
        public static final String LINE = "/lines";
        public static final String STATION = "/stations";
        public static final String SECTION = "/sections";

        private URIMapping() {
        }
    }
}
