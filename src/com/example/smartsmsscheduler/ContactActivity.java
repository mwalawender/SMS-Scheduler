package com.example.smartsmsscheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.SimpleAdapter;

public class ContactActivity extends Activity {

	private static final String TAG = "CONTACT ACTIVITY: ";
	private ArrayList<Map<String, String>> mPeopleList;

	private SimpleAdapter mAdapter;
	private AutoCompleteTextView mTxtPhoneNo;
	Map<String, String> map;
	private Cursor phones, people;

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts);

		mPeopleList = new ArrayList<Map<String, String>>();

		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					populatePeopleList();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		thread.start();

		mTxtPhoneNo = (AutoCompleteTextView) findViewById(R.id.mmWhoNo);

		mAdapter = new SimpleAdapter(this, mPeopleList, R.layout.custom_view,
				new String[] { "Name", "Phone", "Type" }, new int[] {
						R.id.ccontName, R.id.ccontNo, R.id.ccontType });

		mTxtPhoneNo.setAdapter(mAdapter);
		mTxtPhoneNo.setThreshold(1);

		mTxtPhoneNo.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
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

	public void populatePeopleList() {

		Log.i(TAG, "START!");
		mPeopleList.clear();

		try {

			people = getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI, null, null, null,
					null);

			while (people.moveToNext()) {

				String contactName = people
						.getString(people
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
		} catch (Exception e) {
			Log.i(TAG, e.getMessage());
		}
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
