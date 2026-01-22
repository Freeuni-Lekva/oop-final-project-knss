package com.example.registration.DTO;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class User {
    private int id;
    private int roleId;
    private String mail;
    private String username;
    private String passwordHash;
    private boolean isVerified;
    private String verificationCode;
    private LocalDateTime verificationCodeExpiry;
    private Timestamp registerDate;

    public User() {}

    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public User(int id, int roleId, String mail, String username, String passwordHash, boolean isVerified, String verificationCode, LocalDateTime verificationCodeExpiry, Timestamp registerDate) {
        this.id = id;
        this.roleId = roleId;
        this.mail = mail;
        this.username = username;
        this.passwordHash = passwordHash;
        this.isVerified = isVerified;
        this.verificationCode = verificationCode;
        this.verificationCodeExpiry = verificationCodeExpiry;
        this.registerDate = registerDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setIsVerified(boolean active) {
        isVerified = active;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public LocalDateTime getVerificationCodeExpiry() {
        return verificationCodeExpiry;
    }

    public void setVerificationCodeExpiry(LocalDateTime verificationCodeExpiry) {
        this.verificationCodeExpiry = verificationCodeExpiry;
    }

    public Timestamp getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Timestamp registerDate) {
        this.registerDate = registerDate;
    }
}