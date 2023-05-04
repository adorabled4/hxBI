package com.dhx.template.model.DTO;

import lombok.Data;

import java.io.Serializable;

/**
 * @author adorabled4
 * @className JwtToken
 * @date : 2023/05/04/ 16:49
 **/
@Data
public class JwtToken implements Serializable {

    private String token;
    private String refreshToken;

    public JwtToken(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}