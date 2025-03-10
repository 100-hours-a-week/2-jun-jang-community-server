package community.Model.JdbcModel;

import lombok.Getter;
import lombok.Setter;

import java.time.*;
@Getter
@Setter
public abstract class BaseTimeJdbc {
    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    public BaseTimeJdbc() {
        this.createdAt = ZonedDateTime.now(SEOUL_ZONE).toOffsetDateTime();
        this.updatedAt = ZonedDateTime.now(SEOUL_ZONE).toOffsetDateTime();
    }

    // updatedAt을 갱신하는 메서드
    public void updateTimestamp() {
        this.updatedAt = ZonedDateTime.now(SEOUL_ZONE).toOffsetDateTime();
    }
}
