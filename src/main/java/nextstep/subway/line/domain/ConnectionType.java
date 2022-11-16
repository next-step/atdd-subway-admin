package nextstep.subway.line.domain;

public enum ConnectionType {
    FIRST, MIDDLE, LAST, NONE;

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

    public boolean isFirst() {
        return this == ConnectionType.FIRST;
    }

    public boolean isMiddle() {
        return this == ConnectionType.MIDDLE;
    }

    public boolean isLast() {
        return this == ConnectionType.LAST;
    }

}
