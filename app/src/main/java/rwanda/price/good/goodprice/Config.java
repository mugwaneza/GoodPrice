package rwanda.price.good.goodprice;

/**
 * Created by Alexis on 8/19/2016.
 */
public class Config {
    // server URL configuration
    public static final String URL_REQUEST_SMS = "http://f8a9a57d.ngrok.io/GoodPriceApi/request_sms.php";
    public static final String URL_VERIFY_OTP = "http://f8a9a57d.ngrok.io/GoodPriceApi/verify_otp.php";

    // SMS provider identification
    // It should match with your SMS gateway origin
    // You can use  MSGIND, TESTER and ALERTS as sender ID
    // If you want custom sender Id, approve MSG91 to get one
    public static final String SMS_ORIGIN = "ANHIVE";

    // special character to prefix the otp. Make sure this character appears only once in the sms
    public static final String OTP_DELIMITER = ":";
}
