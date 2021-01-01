package nextstep.subway.line.util;

public class ErrorMessage {

  public final static String WRONG_DISTANCE = "역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음";
  public final static String ALREADY_SECTION_EXIST = "상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음";
  public final static String MUST_HAVING_UP_OR_DOWN_STATION = "상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음";

  private ErrorMessage() {
  }
}
