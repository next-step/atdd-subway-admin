package nextstep.subway.line.domain;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class SectionConnector {
    private static final int INIT_NUMBER = 0;

    public static void connectAll(Line line, Section request) {
        List<Section> requests = line.getSections().stream()
                .map(section -> SectionConnector.connect(section, request))
                .filter(Objects::nonNull)
                .collect(toList());

        addNewSections(line, requests);
    }

    private static Section connect(Section current, Section request) {
        ConnectionType connectionType = ConnectionType.match(current, request);

        if(connectionType.isFirst()) {
            return connectFirstSection(request);
        }

        if(connectionType.isMiddle()) {
            return connectMiddleSection(current, request);
        }

        if(connectionType.isLast()) {
            return connectLastSection(request);
        }

        return null;
    }

    private static Section connectFirstSection(Section request) {
        return request;
    }

    private static Section connectMiddleSection(Section current, Section request) {
        current.update(request);
        return request;
    }

    private static Section connectLastSection(Section request) {
        return request;
    }

    private static void addNewSections(Line line, List<Section> requests) {
        IntStream.range(INIT_NUMBER, requests.size())
                .forEach(i -> line.addSection(requests.get(i)));
    }
}
