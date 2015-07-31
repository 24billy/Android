package com.billy.worksheetsenderapp;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EmailSenderActivity extends Activity {

	private SharedPreferences preferences;
	private EditText editRecipient, editSubject, editContent;
	private Button buttonSend, buttonHome;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_email_sender);

		showToast("�w�i�J�l��o�e");

		// init getObject from layout
		editRecipient = (EditText) findViewById(R.id.editRecipient);
		editSubject = (EditText) findViewById(R.id.editSubject);
		editContent = (EditText) findViewById(R.id.editContent);

		// �H�e�l��
		buttonSend = (Button) findViewById(R.id.buttonSend);
		buttonSend.setOnClickListener(sendListener);

		// �^��D�e����
		buttonHome = (Button) findViewById(R.id.buttonHome);
		buttonHome.setOnClickListener(homeListener);

		getPreferenceInfo();
	}

	private Button.OnClickListener sendListener = new Button.OnClickListener() {

		@Override
		public void onClick(View view) {
			if (senderValidator()) {
				recordPreference();

				preferences = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext());
				String fileName = preferences.getString("fileName", "");

				sendEmail(fileName);
			}
		}
	};

	private Button.OnClickListener homeListener = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			finish();
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.assist_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about:
			new AlertDialog.Builder(this)
					.setTitle("���󥻵{��")
					.setMessage(
							"�@�̡GBilly Shih \n���{���}�o�A�ȧ@�ӤH�ϥΡA���o�@������ӷ~�γ~�C\n�]��Allen���Lı�o�C��§����Excel�ɸ�H�@�˪��H�ܷСA�ҥH�g�F�o�����ε{��")
					.setPositiveButton("�T�w",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialogInterface, int i) {

								}
							}).show();
			break;
		case R.id.menu_quit:
			finish();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * �u�H�e�l��v
	 */
	private void sendEmail(String fileName) {
		String[] recipient = { editRecipient.getText().toString() };
		String subject = editSubject.getText().toString();
		String content = editContent.getText().toString();
		content += "\n\n Send from WorkSheetSenderApp";

		Intent emailIntent = new Intent(Intent.ACTION_SEND,
				Uri.parse("mailto:"));

		emailIntent.setType("text/plain");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, recipient);
		emailIntent.putExtra(Intent.EXTRA_CC, "");
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(Intent.EXTRA_TEXT, content);

		/**
		 * Environment.getExternalStorageDirectory().getPath() is equal to
		 * "/sdcard/Android/data/com.billy.worksheetsenderapp/files/"
		 */
		String filePath = "/sdcard/Android/data/com.billy.worksheetsenderapp/files/"
				+ fileName;

		File file = new File(filePath);
		Uri uri = Uri.fromFile(file);

		emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

		try {
			startActivity(Intent.createChooser(emailIntent, "�п�ܶl��o�e�{��"));
			finish();
		} catch (android.content.ActivityNotFoundException ex) {
			showToast("�H��o�e����");
		}
	}

	/**
	 * �u�l��H�e�v���o���n��T
	 */
	private void getPreferenceInfo() {
		preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String recipient = preferences.getString("recipient", "");

		if (!recipient.isEmpty()) {
			editRecipient.setText(preferences.getString("recipient", ""));
			editSubject.setText(preferences.getString("subject", ""));
			editContent.setText(preferences.getString("content", ""));
		}
	}

	/**
	 * �u�l��H�e�v���n��T������
	 */
	private void recordPreference() {
		preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		Editor editor = preferences.edit();

		editor.putString("recipient", editRecipient.getText().toString());
		editor.putString("subject", editSubject.getText().toString());
		editor.putString("content", editContent.getText().toString());

		editor.commit();
	}

	/**
	 * �u�l��H�e�v��T���Ҿ�
	 * 
	 * @return boolean ���ҳq�L(true)/���ҥ���(false)
	 */
	private boolean senderValidator() {
		boolean isValid = true;
		StringBuilder resultText = new StringBuilder();

		if (editRecipient.getText().toString().isEmpty()) {
			resultText.append("�ж�g�u����̡v�C\n");
			isValid = false;
		}
		if (editSubject.getText().toString().isEmpty()) {
			resultText.append("�ж�g�u�D�����C\n");
			isValid = false;
		}
		if (editContent.getText().toString().isEmpty()) {
			resultText.append("�ж�g�u����v�C");
			isValid = false;
		}

		if (!isValid) {
			showToast(resultText.toString());
		}

		return isValid;
	}

	private void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

}