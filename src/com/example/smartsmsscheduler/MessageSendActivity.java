package com.example.smartsmsscheduler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class MessageSendActivity extends Activity implements OnClickListener {

	private static final String TAG = "CONTACT ACTIVITY: ";
	private ArrayList<Map<String, String>> mPeopleList;

	private SimpleAdapter mAdapter;
	private Map<String, String> map;
	private Cursor phones, people;

	private Button btnPickDate, btnPickTime, btnPickFrequency, btnSendMessage;
	private TextView tvDate, tvTime, tvPickedContacts;
	private EditText editTextMessageContent;
	private AutoCompleteTextView mTxtPhoneNo;
	private Spinner frqSpinner;

	// data picker
	private DatePickerDialog datePickerDialog;
	private TimePickerDialog timePickerDialog;
	private SimpleDateFormat dateFormatter;
	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_layout_one);

		initializeViews();

		// data
		setDateTimeField();
		addItemsOnSpinner2();

		// autocomplete text view
		mPeopleList = new ArrayList<Map<String, String>>();

		// launchContactFinderInOwnThread();

		mTxtPhoneNo = (AutoCompleteTextView) findViewById(R.id.mmWhoNo);

		mTxtPhoneNo.setThreshold(1);

		mAdapter = new SimpleAdapter(this, mPeopleList, R.layout.custom_view,
				new String[] { "Name", "Phone", "Type" }, new int[] {
						R.id.ccontName, R.id.ccontNo, R.id.ccontType });

		mTxtPhoneNo.setAdapter(mAdapter);

		Log.i(TAG, "PPL LIST SIZE: " + mPeopleList.size());

		frqSpinner
				.setOnItemSelectedListener(new CustomOnItemSelectedListener());

		mTxtPhoneNo.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.i(TAG, "Clicked");
				@SuppressWarnings("unchecked")
				Map<String, String> map = (Map<String, String>) parent
						.getItemAtPosition(position);
				Iterator<String> myVeryOwnIterator = map.keySet().iterator();
				while (myVeryOwnIterator.hasNext()) {
					String key = (String) myVeryOwnIterator.next();
					String value = (String) map.get(key);
					mTxtPhoneNo.setText(value);

				}
			}
		});
	}

	// add items into spinner dynamically
	public void addItemsOnSpinner2() {

		frqSpinner = (Spinner) findViewById(R.id.frqSpinner);
		List<String> list = new ArrayList<String>();
		list.add("Every day");
		list.add("Every minute");
		list.add("Every 2 minutes");
		list.add("Every 3 minutes");
		list.add("Every 5 minutes");
		list.add("Every 10 minutes");
		list.add("Every 30 minutes");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		frqSpinner.setAdapter(dataAdapter);
	}

	private void setDateTimeField() {

		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		datePickerDialog = new DatePickerDialog(this,
				new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						tvDate.setText(dayOfMonth + "-" + (monthOfYear + 1)
								+ "-" + year);

					}
				}, mYear, mMonth, mDay);

		timePickerDialog = new TimePickerDialog(this,
				new TimePickerDialog.OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						tvTime.setText(hourOfDay + ":" + minute);
					}
				}, mHour, mMinute, false);
	}

	private void initializeViews() {

		btnPickDate = (Button) findViewById(R.id.btnPickDate);
		btnPickTime = (Button) findViewById(R.id.btnPickTime);
		btnPickFrequency = (Button) findViewById(R.id.btnPickFrequency);
		btnSendMessage = (Button) findViewById(R.id.btnSendMessage);

		tvDate = (TextView) findViewById(R.id.tvDate);
		// tvFrequency = (TextView) findViewById(R.id.tvFrequency);
		tvTime = (TextView) findViewById(R.id.tvTime);
		tvPickedContacts = (TextView) findViewById(R.id.tvPickedContacts);

		editTextMessageContent = (EditText) findViewById(R.id.messageContent);

		btnPickDate.setOnClickListener(this);
		btnPickFrequency.setOnClickListener(this);
		btnPickTime.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnPickDate:
			datePickerDialog.show();
			break;
		case R.id.btnPickTime:
			timePickerDialog.show();
			break;
		case R.id.btnSendMessage:
			sendMessage();
			break;
		default:
			break;
		}

	}

	private void sendMessage() {
		String destinationNumber = mTxtPhoneNo.getText().toString();
		String messageContent = editTextMessageContent.getText().toString();
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(destinationNumber, null, messageContent,
				null, null);
	}

	public void populatePeopleList() {

		Log.i(TAG, "START!");
		mPeopleList.clear();

		people = getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

		while (people.moveToNext()) {

			String contactName = people.getString(people
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			String contactId = people.getString(people
					.getColumnIndex(ContactsContract.Contacts._ID));
			String hasPhone = people
					.getString(people
							.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

			if ((Integer.parseInt(hasPhone) > 0)) {

				// You know have the number so now query it like this
				phones = getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = " + contactId, null, null);
				while (phones.moveToNext()) {

					// store numbers and display a dialog letting the user
					// select which.
					String phoneNumber = phones
							.getString(phones
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

					String numberType = phones
							.getString(phones
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

					Map<String, String> NamePhoneType = new HashMap<String, String>();

					NamePhoneType.put("Name", contactName);
					NamePhoneType.put("Phone", phoneNumber);

					if (numberType.equals("0"))
						NamePhoneType.put("Type", "Work");
					else if (numberType.equals("1"))
						NamePhoneType.put("Type", "Home");
					else if (numberType.equals("2"))
						NamePhoneType.put("Type", "Mobile");
					else
						NamePhoneType.put("Type", "Other");

					// Then add this map to the list.
					mPeopleList.add(NamePhoneType);
				}
				phones.close();
			}
		}
		people.close();
		// startManagingCursor(people);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (people != null) {
			people.close();
		}
		if (phones != null) {
			phones.close();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (people != null) {
			people.close();
		}
		if (phones != null) {
			phones.close();
		}
	}

}
