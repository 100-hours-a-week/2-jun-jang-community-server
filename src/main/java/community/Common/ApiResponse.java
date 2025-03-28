package community.Common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private final String message = "성공했습니다.";
    private boolean isSuccess = true;
    private String code = null;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private OffsetDateTime responseAt = OffsetDateTime.now();
    private T data = null;

    public ApiResponse(T data, String code) {
        this.data = data;
        this.code = code;
    }
}