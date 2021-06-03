//package nextstep.subway.line.domain;
//
//import nextstep.subway.line.dto.LineRequest;
//
//public enum LineSeoul {
//    NUMBER_2("2호선", "다크그린"),
//    NUMBER_6("6호선", "오렌지");
//
//    private String lineName;
//    private String color;
//
//    LineSeoul(String lineName, String color) {
//        this.lineName = lineName;
//        this.color = color;
//    }
//
//    public LineRequest toRequest() {
//        return new LineRequest(lineName, color);
//    }
//
//    public Line toLine() {
//        return new Line(lineName, color);
//    }
//
//    public String lineName() {
//        return this.lineName;
//    }
//
//    public String color() {
//        return this.color;
//    }
//}
