package cimb.niaga.app.billsplit.corecycle;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

/**
 * Created by Lenovo Thinkpad on 1/15/2016.
 */
public class FormatCurrency {
    public static String formatAmount(String amount) {
        String ret = amount;
        if (null == amount || 0 == amount.trim().length()) {
            return "0.00";
        }
        ret = renderAmount(amount, ',');
        String prePart      = "";
        if (-1 != ret.indexOf('-')) {
            prePart = "-";
            ret = renderAmount(amount, '-');
        }
        try {
            Double.valueOf(ret);
        } catch (Exception e) {
            return amount;
        }
        String integerPart  = "";
        String fractionPart = "";

        if (-1 != ret.indexOf('.')) {
            integerPart  = Long.parseLong("0" + ret.substring(0, ret.indexOf('.')))+""; // format 00## to ##
            fractionPart = ret.substring(ret.indexOf('.')+1)+"0";
        } else {
            integerPart = ret;
        }
        return prePart + parseAmount(integerPart, ",") + "." + parseNumber(fractionPart, "0", 2);
    }


    private static String renderAmount(String amount, char flag) {
        String ret = amount;
        while(-1 != ret.indexOf(flag)) {
            ret = ret.substring(0, ret.indexOf(flag)) + ret.substring(ret.indexOf(flag)+1);
        }
        return ret;
    }

    private static String parseAmount(String inStr, String split) {
        String ret = inStr;
        for(int i= inStr.length() - 3; i> 0; i-= 3) {
            ret = ret.substring(0, i) + split + ret.substring(i);
        }
        return ret;
    }

    private static String parseNumber(String inStr, String appendDefaultValue, int len) {
        StringBuffer ret = new StringBuffer();
        if (len < inStr.trim().length()) {
            return inStr.trim().substring(0, len);
        }
        ret.append(inStr);
        for (int i=0; i<len-inStr.length(); i++) {
            ret.append(appendDefaultValue);
        }
        return ret.toString();
    }

    public static String getCurrencyFormattedValue(double number, boolean zero) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) numberFormat).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) numberFormat).setDecimalFormatSymbols(decimalFormatSymbols);
        String value = numberFormat.format(number);
        if (zero && number == 0) {
            return "";
        }
        return value.replaceAll("\\(", "").replaceAll("\\)", "");
    }

    public static String getCurrencyFormattedValue(String number, boolean zero) {
        number = number.replaceAll("\\(", "").replaceAll("\\)", "");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) numberFormat).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) numberFormat).setDecimalFormatSymbols(decimalFormatSymbols);
        Double currentBalanceValue = Double.parseDouble(number);
        number = numberFormat.format(currentBalanceValue);
        if (zero && currentBalanceValue == 0) {
            return "";
        }
        return number.replaceAll("\\(", "").replaceAll("\\)", "");
    }

    public static String CurrencyIDR(String jumlah){
        double harga = Double.parseDouble(jumlah);
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator('.');
        formatRp.setGroupingSeparator(',');

        kursIndonesia.setDecimalFormatSymbols(formatRp);
        String hasil = kursIndonesia.format(harga);
        return hasil;

    }

    public static String getRupiahFormat(String number) {
        String displayedString = "";

        if (number.length() == 0) {
            displayedString = "Rp. 0";
        } else {
            if (number.length() > 3) {
                int length = number.length();

                for (int i = length; i > 0; i -= 3) {
                    if (i > 3) {
                        String myStringPrt1 = number.substring(0, i - 3);
                        String myStringPrt2 = number.substring(i - 3);

                        String combinedString;

                        combinedString = myStringPrt1 + ".";

                        combinedString += myStringPrt2;
                        number = combinedString;

                        displayedString = "Rp. " + combinedString;
                    }
                }
            } else {
                displayedString = "Rp. " + number;
            }
        }
        return displayedString;
    }
}
