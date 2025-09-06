package taskmanagement.Util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHelper {

    public static <T> ResponseEntity<T> success(T data) {
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

}
