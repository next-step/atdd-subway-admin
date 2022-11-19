package nextstep.subway.line.domain;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

public class SectionConnectManager {
    public static void connectAll(Line line, Section request) {
        List<Section> requests = line.getSections().stream()
                .map(section -> SectionConnectManager.connect(section, request))
                .filter(Objects::nonNull)
                .collect(toList());

        addNewSections(line, requests);
    }

    private static Section connect(Section current, Section request) {
        ConnectionType connectionType = ConnectionType.match(current, request);
        return connectionType.connect(current, request);
    }

    public static Section connectFirstSection(Section request) {
        return request;
    }

    public static Section connectMiddleSection(Section current, Section request) {
        current.divideSection(request);
        return request;
    }

    public static Section connectLastSection(Section request) {
        return request;
    }

    private static void addNewSections(Line line, List<Section> requests) {
        requests.forEach(request -> line.addSection(request));
    }
}
