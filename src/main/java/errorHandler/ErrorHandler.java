package errorHandler;

/**
 * Created by Alireza on 6/28/2015.
 */
public final class ErrorHandler {
    private static ErrorHandler instance;
    public boolean hasError = false;

    private ErrorHandler() {

    }

    public static ErrorHandler getInstance() {
        if (instance == null) {
            instance = new ErrorHandler();
        }
        return instance;
    }

    public void printError(String msg) {
        hasError = true;
        System.out.println(msg);
    }
}
