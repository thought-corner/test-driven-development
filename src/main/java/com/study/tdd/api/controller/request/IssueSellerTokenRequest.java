package com.study.tdd.api.controller.request;

import com.study.tdd.application.query.IssueSellerToken;

public record IssueSellerTokenRequest(String email, String password) {

    public IssueSellerToken toQuery() {
        return new IssueSellerToken(email, password);
    }
}
