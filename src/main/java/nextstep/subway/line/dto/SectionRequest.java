package nextstep.subway.line.dto;

public class SectionRequest {
  private final long upStationId;
  private final long downStationId;
  private final int distance;

  public SectionRequest(long upStationId, long downStationId, int distance) {
    this.upStationId = upStationId;
    this.downStationId = downStationId;
    this.distance = distance;
  }

  public long getUpStationId() {
    return upStationId;
  }

  public long getDownStationId() {
    return downStationId;
  }

  public int getDistance() {
    return distance;
  }
}
