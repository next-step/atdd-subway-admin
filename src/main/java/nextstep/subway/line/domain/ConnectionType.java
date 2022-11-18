package nextstep.subway.line.domain;

import java.util.function.BiFunction;

public enum ConnectionType {
    FIRST((current, request) -> SectionConnectManager.connectFirstSection(request)),
    MIDDLE((current, request) -> SectionConnectManager.connectMiddleSection(current, request)),
    LAST((current, request) -> SectionConnectManager.connectLastSection(request)),
    NONE((current, request) -> null);

    private BiFunction<Section, Section, Section> expression;

    ConnectionType(BiFunction<Section, Section, Section> expression) {
        this.expression = expression;
    }

    public static ConnectionType match(Section current, Section request) {
        if(judgeMiddle(current, request)) {
            return ConnectionType.MIDDLE;
        }

        if(judgeFirst(current, request)) {
            return ConnectionType.FIRST;
        }

        if(judgeLast(current, request)) {
            return ConnectionType.LAST;
        }

        return ConnectionType.NONE;
    }

    private static boolean judgeFirst(Section current, Section request) {
        return current.getUpStation().equals(request.getDownStation());
    }

    private static boolean judgeMiddle(Section current, Section request) {
        return current.getUpStation().equals(request.getUpStation()) ||
                current.getDownStation().equals(request.getDownStation());
    }

    private static boolean judgeLast(Section current, Section request) {
        return current.getDownStation().equals(request.getUpStation());
    }

    public Section connect(Section current, Section request) {
        return expression.apply(current, request);
    }
}
