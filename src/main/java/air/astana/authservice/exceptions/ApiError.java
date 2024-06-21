package air.astana.authservice.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ApiError implements Serializable {
    private UUID exceptionId;
    private int statusCode;
    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT")
    private Date timestamp;
    private String message;
    private String url;
    private Map<String, Object> errorDetails;

    public ApiError(UUID exceptionId, HttpStatus status, String message, String url, Map<String, Object> errorDetails) {
        this.timestamp = new Date();
        this.errorDetails = errorDetails;
        this.statusCode = status.value();
        this.status = status;
        this.exceptionId = exceptionId;
        this.message = message;
        this.url = url;
    }

    public ApiError(HttpStatus status, String message, String url, Map<String, Object> errorDetails) {
        this.timestamp = new Date();
        this.errorDetails = errorDetails;
        this.statusCode = status.value();
        this.status = status;
        this.message = message;
        this.url = url;
    }

    public ApiError(HttpStatus status, String message, String url) {
        this.timestamp = new Date();
        this.statusCode = status.value();
        this.status = status;
        this.message = message;
        this.url = url;
    }
}
