import java.io.IOException;

public class ResourceNotFoundException extends IOException {
    public ResourceNotFoundException(String message) {
        super("Resource Not Found!! " + message);
    }
    public ResourceNotFoundException() {
        super("Resource Not Found!!");
    }

}
