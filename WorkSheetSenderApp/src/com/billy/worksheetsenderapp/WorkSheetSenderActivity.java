package com.billy.worksheetsenderapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.util.IOUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class WorkSheetSenderActivity extends Activity {

	private Button buttonWorkSheet, buttonSender;
	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_work_sheet_sender);

		showToast("�w��ϥΥ��{��");

		buttonWorkSheet = (Button) findViewById(R.id.buttonWorkSheet);
		buttonSender = (Button) findViewById(R.id.buttonSender);

		buttonWorkSheet.setOnClickListener(workSheetListener);
		buttonSender.setOnClickListener(emailSenderListener);

		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		if (!preferences.contains("fileName")) {
			loadDefaultSetting();
		}
	}

	private void loadDefaultSetting() {
		// clear preferences and init fileName
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Editor editor = preferences.edit();
		String fileName = "myExcel.xls";

		editor.clear();
		
		editor.putString("fileName", fileName);
		editor.commit();

		// delete all excel files then generate template file
		String dirPath = "/sdcard/Android/data/com.billy.worksheetsenderapp/files/";
		File files = new File(dirPath);
		File[] listOfFiles = files.listFiles();

		if (files != null && files.isDirectory()) {
			for (File file : listOfFiles) {
				if (file.isFile() && file.exists()) {
					file.delete();
				}
			}
		}
		
		generateExcelTemplate(fileName);
	}

	// �����e����u�@��x��ť��
	private Button.OnClickListener workSheetListener = new Button.OnClickListener() {

		@Override
		public void onClick(View view) {
			Intent intent = new Intent();
			intent.setClass(WorkSheetSenderActivity.this,
					WorkSheetActivity.class);
			startActivity(intent);
		}
	};

	// �����e����H�e�l���ť��
	private Button.OnClickListener emailSenderListener = new Button.OnClickListener() {

		@Override
		public void onClick(View view) {
			Intent intent = new Intent();
			intent.setClass(WorkSheetSenderActivity.this,
					EmailSenderActivity.class);
			startActivity(intent);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.work_sheet_sender, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about:
			new AlertDialog.Builder(this)
					.setTitle("���󥻵{��")
					.setMessage("�@�̡GBilly Shih \n���{���}�o�A�ȧ@�ӤH�ϥΡA���o�@������ӷ~�γ~�C")
					.setPositiveButton("�T�w",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialogInterface, int i) {

								}
							}).show();
			break;
		case R.id.menu_recovery:
			new AlertDialog.Builder(this)
					.setTitle("�^�_��l�]�w")
					.setMessage("�u�@��x�P�l��]�w��T�N�|�Q�M���A�Ы��T�w�~�����C")
					.setPositiveButton("�T�w",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialogInterface, int i) {
									loadDefaultSetting();
								}
							})
					.setNegativeButton("����",
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

	private void generateExcelTemplate(String fileName) {
		InputStream is = getResources().openRawResource(R.raw.sample);
		OutputStream out = null;

		try {
			out = new FileOutputStream(new File(this.getExternalFilesDir(null),
					fileName));
			Toast.makeText(this, "myExcel is creating....", Toast.LENGTH_SHORT)
					.show();
			IOUtils.copy(is, out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			is.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

}