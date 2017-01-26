package cimb.niaga.app.billsplit.corecycle;

/**
 * Created by Admin on 13/01/2017.
 */

public class MyAPIClient {
    public static final int HTTP_DEFAULT_TIMEOUT = 60 * 1000;
    public static final boolean PROD_FLAG_ADDRESS = false;

    public static final String headaddress = "http://splitbill-backend.mybluemix.net/api";
    public static final String headocr = "https://splitbill-loadscan.mybluemix.net";

    public static final String LINK_UPLOADER= headocr + "/upload";
    public static final String LINK_OCR = headocr + "/receipt";
    public static final String LINK_TEST = headaddress+"/notes";
    public static final String LINK_CHECKDEVICE = headaddress+"/Services/splashScreen";
    public static final String LINK_REGISTER = headaddress+"/Services/registerUser";
    public static final String LINK_DASHBOARDOWED = headaddress+"/Services/dashboardOwed";


    //GLOBAL VARIABLE
    public static String email = "email";
    public static String nickname = "nickname";
    public static String username = "username";
    public static String imei = "imei";
    public static String id = "id";

}
