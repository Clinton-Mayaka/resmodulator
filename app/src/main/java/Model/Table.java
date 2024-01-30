package Model;

public class Table {
    private String Name;
    private String Password;
    private String TableNo;
    private String secureCode;

    public Table() {
    }

    public Table(String name, String password, String secureCode) {
        Name = name;
        Password = password;
        this.secureCode = secureCode;
    }

    public String getSecureCode() {
        return secureCode;
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getTableNo() {
        return TableNo;
    }

    public void setTableNo(String tableNo) {
        TableNo = tableNo;
    }
}
