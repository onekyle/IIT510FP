package com.wkyle.bankrecord.models;

import java.util.Objects;

public class AccountModel {
    public enum RoleType {
        ADMIN, // 0
        ACCOUNT_MANAGER, // 1
        CUSTOMER, // 2
    }

    private int cid;
    private String uname;
    private String passwdEncrypted;
    private RoleType roleType;
    private String roleTypeString;

    public String getRoleTypeString() {
        return roleTypeString;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getPasswdEncrypted() {
        return passwdEncrypted;
    }

    public void setPasswdEncrypted(String passwdEncrypted) {
        this.passwdEncrypted = passwdEncrypted;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
        this.roleTypeString = roleType.name();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AccountModel that = (AccountModel) o;
        return cid == that.cid && Objects.equals(uname, that.uname) && Objects.equals(passwdEncrypted, that.passwdEncrypted) && roleType == that.roleType && Objects.equals(roleTypeString, that.roleTypeString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cid, uname, passwdEncrypted, roleType, roleTypeString);
    }
}
