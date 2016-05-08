package oss.bus.school.schoolbus;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

//import android.app.FragmentManager;
//import android.app.FragmentTransaction;


public class Bus extends ActionBarActivity implements SelectBus.OnFragmentAction{

    FragmentManager fragmentManager;
    PendingIntent pendingIntent;
    NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        SelectBus select_bus_fragment=new SelectBus();
        fragmentTransaction.add(R.id.fragmentPlace,select_bus_fragment,"mamun");
        fragmentTransaction.commit();

    }
/*
    public void Notify(String notificationTitle, String notificationMessage) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        @SuppressWarnings("deprecation")
        Notification notification = new Notification(R.drawable.ic_launcher,
                "New Message", System.currentTimeMillis());

        Intent notificationIntent = new Intent(this, Bus.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        notification.setLatestEventInfo(Bus.this, notificationTitle,
                notificationMessage, pendingIntent);
        notificationManager.notify(9999, notification);
    }*/

    @Override
    public void setBusMap(String id, String pass, String schoolName, String busNumber) {

    }

    @Override
    public void Notify(String notificationTitle, String notificationMessage) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        @SuppressWarnings("deprecation")
        Notification notification = new Notification(R.drawable.ic_launcher,
                "New Message", System.currentTimeMillis());

        Intent notificationIntent = new Intent(this, Bus.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        notification.setLatestEventInfo(Bus.this, notificationTitle,
                notificationMessage, pendingIntent);
        notification.defaults = Notification.DEFAULT_SOUND;

        notificationManager.notify(9999, notification);



        /*Alert dialog*/
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Bus.this);

        alertDialogBuilder.setTitle(/*this.getTitle()+ */notificationTitle);

        alertDialogBuilder.setMessage(notificationMessage);
        /*
        alertDialogBuilder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog,int id) {


            }
        });
*/

        alertDialogBuilder.setNeutralButton("Got It",new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog,int id) {


            }

        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
}
