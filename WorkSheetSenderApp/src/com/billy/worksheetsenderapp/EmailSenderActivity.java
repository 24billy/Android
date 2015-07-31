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

		showToast("已進入郵件發送");

		// init getObject from layout
		editRecipient = (EditText) findViewById(R.id.editRecipient);
		editSubject = (EditText) findViewById(R.id.editSubject);
		editContent = (EditText) findViewById(R.id.editContent);

		// 寄送郵件
		buttonSend = (Button) findViewById(R.id.buttonSend);
		buttonSend.setOnClickListener(sendListener);

		// 回到主畫面用
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
					.setTitle("關於本程式")
					.setMessage(
							"作者：Billy Shih \n本程式開發，僅作個人使用，不得作為任何商業用途。\n因為Allen說他覺得每個禮拜改Excel檔跟寄一樣的信很煩，所以寫了這個應用程式")
					.setPositiveButton("確定",
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
	 * 「寄送郵件」
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
			startActivity(Intent.createChooser(emailIntent, "請選擇郵件發送程式"));
			finish();
		} catch (android.content.ActivityNotFoundException ex) {
			showToast("信件發送失敗");
		}
	}

	/**
	 * 「郵件寄送」取得偏好資訊
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
	 * 「郵件寄送」偏好資訊紀錄器
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
	 * 「郵件寄送」資訊驗證器
	 * 
	 * @return boolean 驗證通過(true)/驗證失敗(false)
	 */
	private boolean senderValidator() {
		boolean isValid = true;
		StringBuilder resultText = new StringBuilder();

		if (editRecipient.getText().toString().isEmpty()) {
			resultText.append("請填寫「收件者」。\n");
			isValid = false;
		}
		if (editSubject.getText().toString().isEmpty()) {
			resultText.append("請填寫「主旨﹞。\n");
			isValid = false;
		}
		if (editContent.getText().toString().isEmpty()) {
			resultText.append("請填寫「內文」。");
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