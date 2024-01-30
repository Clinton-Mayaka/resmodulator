package Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

import Model.Table;
import Model.User;
import Remote.APIService;
import Remote.RetrofitClient;

public class Common {
    private static  final String BASE_URL ="https://fcm.googleapis.com/";

    public static String STAFF_NO_TEXT ="UserStaff No";

    public static  boolean isConnectedToInternet(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null)
            {
                for (int i=0;i<info.length;i++)
                {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    public  static  final  String DELETE = "Delete";

    public static APIService getFCMService()
    {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static User currentUser;

    public static Table currentTable;

    public static String getDate(long time)
    {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date = new StringBuilder(DateFormat.format("dd-MM-yyyy HH:mm"
                ,calendar)
                .toString());
        return date.toString();
    }

    public static String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "Placed";
        else if(status.equals("1"))
            return "On the way";
        else
            return "Ready";

    }

}
