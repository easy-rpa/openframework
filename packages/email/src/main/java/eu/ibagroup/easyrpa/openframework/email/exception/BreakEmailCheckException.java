package eu.ibagroup.easyrpa.openframework.email.exception;

public class BreakEmailCheckException extends RuntimeException{

    private boolean includeIntoResult;

    public BreakEmailCheckException(boolean includeIntoResult) {
        this.includeIntoResult = includeIntoResult;
    }

    public boolean isIncludeIntoResult() {
        return includeIntoResult;
    }
}
