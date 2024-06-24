package air.astana.authservice.exceptions;

import java.util.Map;
import java.util.UUID;

public class UnauthorizedException extends RuntimeException {
    public UUID exceptionId;
    public Map<String, Object> errorDetails;

    public UnauthorizedException(String message) {
        super(message);
        this.exceptionId = UUID.randomUUID();
    }

    public UnauthorizedException(String message, Map<String, Object> errorDetails) {
        super(message);
        this.exceptionId = UUID.randomUUID();
        this.errorDetails = errorDetails;
    }
}
