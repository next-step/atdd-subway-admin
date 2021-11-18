package nextstep.subway.line.domain;

import nextstep.subway.line.dto.LineResponse;

import java.util.*;
import java.util.stream.Collectors;

/**
 * packageName : nextstep.subway.line.domain
 * fileName : Lines
 * author : haedoang
 * date : 2021-11-18
 * description :
 */
public class Lines {
    private final List<Line> lines;

    private Lines(List<Line> lineList) {
        this.lines = new ArrayList<>(validate(lineList));
    }

    public static Lines of(List<Line> lineList) {
        return new Lines(lineList);
    }

    private List<Line> validate(List<Line> lineList) {
        return Optional.ofNullable(lineList).orElseGet(Collections::emptyList)
                .stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<LineResponse> toDto() {
        return this.lines.stream().map(LineResponse::of).collect(Collectors.toList());
    }
}
