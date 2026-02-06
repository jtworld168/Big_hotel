package org.zeushotel.fastmart.nucleus.dataschema;

public enum UserRole {
    ORDINARY(0, "普通用户", "Ordinary User"),
    EMPLOYEE(1, "员工", "Employee"),
    ADMIN(2, "管理员", "Administrator");
    
    private final Integer code;
    private final String chineseLabel;
    private final String englishLabel;
    
    UserRole(Integer code, String chineseLabel, String englishLabel) {
        this.code = code;
        this.chineseLabel = chineseLabel;
        this.englishLabel = englishLabel;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getChineseLabel() {
        return chineseLabel;
    }
    
    public String getEnglishLabel() {
        return englishLabel;
    }
    
    public static UserRole fromCode(Integer code) {
        if (code == null) {
            return ORDINARY;
        }
        for (UserRole role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        return ORDINARY;
    }
}
