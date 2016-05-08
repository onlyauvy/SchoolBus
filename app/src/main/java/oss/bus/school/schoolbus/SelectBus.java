package oss.bus.school.schoolbus;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Abdullah on 10/11/2015.
 */
public class SelectBus extends Fragment {
    LinearLayout lin;
    DatePickerDialog mDateSetListener;
    EditText date;
    OnFragmentAction onFragmentAction;
    Button go_map;
    public int mYear;
    public int mMonth;
    public int mDay;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.select_bus,container,false);

        lin=(LinearLayout)view.findViewById(R.id.lin);
        lin.setAlpha(50);
        TextView school_name=(TextView)view.findViewById(R.id.school_name);
        final Spinner bus_number=(Spinner)view.findViewById(R.id.bus_number);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, Store.busNumber);

        school_name.setText(""+Store.schoolName);
        bus_number.setAdapter(dataAdapter);

        go_map = (Button)view.findViewById(R.id.goMap);
        go_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(date.getText().toString().compareTo("")!=0) {
                    Store.mapDate = date.getText().toString();
                    Store.mapBus = bus_number.getSelectedItem().toString();

                    Intent intent=new Intent(getActivity(), BusMapsActivity.class);
                    startActivity(intent);
/*
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment busMap=new BusMap();
                    fragmentTransaction.replace(R.id.fragmentPlace,busMap);
                    fragmentTransaction.commit();
                    */
                }
                else{
                    Toast.makeText(getActivity(),"Please Select the Date first",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //updateDisplay();
        date=(EditText)view.findViewById(R.id.date);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDateSetListener.show();
            }
        });

        Calendar newCalendar = Calendar.getInstance();
        mDateSetListener = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                updateDisplay();
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        return view;
    }

    private void updateDisplay() {
        GregorianCalendar c = new GregorianCalendar(mYear, mMonth, mDay);
        SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yyyy");
        date.setText(sdf.format(c.getTime()));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onFragmentAction.Notify("Message from School","Tomorrow Bus will be late" +
                "PM class on October 24th, Saturday to make up for the class we'll be missing the very next day for DB mid-term.\n" +
                "\n" +
                "Date: October, 24th\n" +
                "Time: 6:30 p.m.\n" +
                "\n" +
                "P.S.\n" +
                "\n" +
                "There was an assignment due; a quiz due as well. No directives but do come prepared.");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onFragmentAction=(OnFragmentAction)activity;
    }

    public interface OnFragmentAction{
        public void setBusMap(String id, String pass, String schoolName, String busNumber);
        public void Notify(String notificationTitle, String notificationMessage);
    }
}
