package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;

public class Lines {
    private List<Line> lines;

    public Lines() {
        this.lines = new ArrayList<>();
    }

    public Lines(List<Line> lines) {
        this.lines = lines;
    }
}
