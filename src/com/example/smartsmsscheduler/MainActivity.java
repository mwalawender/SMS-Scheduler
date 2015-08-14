package com.example.smartsmsscheduler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 
 * @author Michaœ no to jedziemy z koksikiemmmmmmmmmmmmmmmmmmmmm....
 */
public class MainActivity extends ActionBarActivity implements OnClickListener {

	private EditText editTextMessage, editTextPhoneNumber;
	private Button btnSendMessage, btnAutoComplete, btnAutoComplete2;
	private SmsManager smsManager;

	private String messageContent, phoneNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btnSendMessage = (Button) findViewById(R.id.btnSend);
		btnAutoComplete = (Button) findViewById(R.id.btnAutoComplete);
		btnAutoComplete2 = (Button) findViewById(R.id.btnAutoComplete2);

		editTextMessage = (EditText) findViewById(R.id.editTextMessage);
		editTextPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);

		btnSendMessage.setOnClickListener(this);
		btnAutoComplete.setOnClickListener(this);
		btnAutoComplete2.setOnClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnSend:
			sendMessage();
			break;
		case R.id.btnAutoComplete:
			startActivity(new Intent(getApplicationContext(),
					MessageSendActivity.class));
			break;
		case R.id.btnAutoComplete2:
			startActivity(new Intent(getApplicationContext(),
					ContactActivity.class));
			break;
		default:
			break;
		}
	}

	private void sendMessage() {
		phoneNumber = editTextPhoneNumber.getText().toString();
		messageContent = editTextMessage.getText().toString();

		try {
			smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(phoneNumber, null, messageContent, null,
					null);
			Toast.makeText(getApplicationContext(), "Message Sent",
					Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage().toString(),
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}

	}
}
