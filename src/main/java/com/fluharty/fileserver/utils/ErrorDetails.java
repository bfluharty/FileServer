package com.fluharty.fileserver.utils;

import org.springframework.http.HttpStatus;

public class ErrorDetails {
    private final String title;
    private final String explanation;
    private final HttpStatus status;
    private final String stacktrace;

    private ErrorDetails(Builder builder) {
        this.title = builder.title;
        this.explanation = builder.explanation;
        this.status = builder.status;
        this.stacktrace = builder.stacktrace;
    }

    public static final class Builder {
        private final String title;
        private final String explanation;
        private HttpStatus status;
        private String stacktrace;

        public Builder(String title, String explanation) {
            this.title = title;
            this.explanation = explanation;
        }

        public Builder(ErrorDetails error) {
            this.title = error.title;
            this.explanation  = error.explanation;
        }

        public Builder status(HttpStatus val){
            this.status = val;
            return this;
        }

        public Builder stacktrace(String val){
            this.stacktrace = val;
            return this;
        }

        public ErrorDetails build(){
            return new ErrorDetails(this);
        }
    }

    public String getTitle() {
        return title;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getStacktrace() {
        return stacktrace;
    }
}
