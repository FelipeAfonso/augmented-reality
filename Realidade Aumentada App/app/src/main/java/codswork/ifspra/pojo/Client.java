package codswork.ifspra.pojo;

/**
 * Created by Juliano on 16/08/2016.
 */
public class Client {

    //stating the names of the database fields


    public static String NAME = "Name";
    public static String EMAIL = "Email";
    public static String PASSWORD = "Password";
    public static String STREETNAME = "StreetName";
    public static String NUMBER = "Number";
    public static String COMPLEMENT = "Complement";
    public static String ZIPCODE = "ZipCode";
    public static String NAMENeighborhood = "NameNeighborhood";
    public static String NAMECity = "NameCity";
    public static String NAMEState = "NameState";
    public static String IDCLIENT = "idClient";






    private boolean AuthenticationJsonData;
    private int idClient;
    private String Name;
    private String Email;
    private String Password;
    private String StreetName;
    private String Number;
    private String Complement;
    private String ZipCode;
    private String NameNeighborhood;
    private String NameCity;
    private String NameState;






    public boolean isAuthenticationJsonData() {
        return AuthenticationJsonData;
    }

    public void setAuthenticationJsonData(boolean authenticationJsonData) {
        AuthenticationJsonData = authenticationJsonData;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getStreetName() {
        return StreetName;
    }

    public void setStreetName(String streetName) {
        StreetName = streetName;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getComplement() {
        return Complement;
    }

    public void setComplement(String complement) {
        Complement = complement;
    }

    public String getZipCode() {
        return ZipCode;
    }

    public void setZipCode(String zipCode) {
        ZipCode = zipCode;
    }

    public String getNameNeighborhood() {
        return NameNeighborhood;
    }

    public void setNameNeighborhood(String nameNeighborhood) {
        NameNeighborhood = nameNeighborhood;
    }

    public String getNameCity() {
        return NameCity;
    }

    public void setNameCity(String nameCity) {
        NameCity = nameCity;
    }

    public String getNameState() {
        return NameState;
    }

    public void setNameState(String nameState) {
        NameState = nameState;
    }





}
