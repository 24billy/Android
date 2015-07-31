package com.billy.worksheetsenderapp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.billy.worksheetsenderapp.vo.WorkSheetVO;

public class WorkSheetActivity extends Activity {

	private Button buttonHome, buttonSubmit;
	private EditText editFileName, editConsultantName, editStartDate;
	private TableLayout tableData;

	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_work_sheet);

		showToast("已進入工作日誌");
		buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
		buttonHome = (Button) findViewById(R.id.buttonHome);

		editFileName = (EditText) findViewById(R.id.editFileName);
		editConsultantName = (EditText) findViewById(R.id.editConsultantName);
		editStartDate = (EditText) findViewById(R.id.editStartDate);

		tableData = (TableLayout) findViewById(R.id.tableData);

		buttonSubmit.setOnClickListener(submitListener);
		buttonHome.setOnClickListener(homeListener);

		showWorkSheetData();
	}

	private Button.OnClickListener submitListener = new Button.OnClickListener() {

		@Override
		public void onClick(View view) {
			// 先處理檔名
			preferences = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			String source = preferences.getString("fileName", "");

			Editor editor = preferences.edit();

			String fileName = editFileName.getText().toString();
			editor.putString("fileName", fileName);
			editor.commit();

			WorkSheetVO workSheet = new WorkSheetVO();

			// 先加入主檔資訊
			workSheet.setFileName(fileName);

			SimpleDateFormat sdformat = new SimpleDateFormat("yyyy/MM/dd");
			Date startDate = new Date();

			try {
				startDate = sdformat.parse(editStartDate.getText().toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}

			workSheet.setStartDate(startDate);
			workSheet
					.setConsultantName(editConsultantName.getText().toString());

			// 再加入工作日誌內容
			double[] hours = new double[5];
			String[] system = new String[5];
			String[] deliverable = new String[5];

			for (int index = 0; index < 5; index++) {
				TableRow row = (TableRow) tableData.getChildAt(index + 1);

				EditText col2 = (EditText) row.getChildAt(1);
				EditText col3 = (EditText) row.getChildAt(2);
				EditText col4 = (EditText) row.getChildAt(3);

				hours[index] = Double.parseDouble(col2.getText().toString());
				system[index] = col3.getText().toString();
				deliverable[index] = col4.getText().toString();
			}
			workSheet.setHours(hours);
			workSheet.setSystem(system);
			workSheet.setDeliverables(deliverable);

			// 送出修改內容
			editExcelFile(workSheet, source);

			// 更新畫面資訊
			showWorkSheetData();
			showToast("工作日誌已更新");
		}

	};

	private Button.OnClickListener homeListener = new Button.OnClickListener() {

		@Override
		public void onClick(View view) {
			finish();
		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.assist_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about:
			new AlertDialog.Builder(this)
					.setTitle("關於本程式")
					.setMessage("作者：Billy Shih \n本程式開發，僅作個人使用，不得作為任何商業用途。")
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
	 * 顯示「工作日誌」內容
	 */
	private void showWorkSheetData() {
		preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String fileName = preferences.getString("fileName", "");

		editFileName.setText(fileName);

		// 從檔案取得工作日誌物件
		WorkSheetVO workSheetVO = readExcelFile(fileName);

		editConsultantName.setText(workSheetVO.getConsultantName());

		SimpleDateFormat sdformat = new SimpleDateFormat("yyyy/MM/dd");
		Date startDate = workSheetVO.getStartDate();
		Date currentDate = new Date();
		currentDate = startDate;

		// add(Calendar.DATE, 1)
		editStartDate.setText(sdformat.format(startDate.getTime()));

		for (int index = 0; index < 5; index++) {
			TableRow row = (TableRow) tableData.getChildAt(index + 1);
			TextView col1 = (TextView) row.getChildAt(0);

			EditText col2 = (EditText) row.getChildAt(1);
			EditText col3 = (EditText) row.getChildAt(2);
			EditText col4 = (EditText) row.getChildAt(3);

			col1.setText(sdformat.format(currentDate));
			// add one day
			Calendar cal = Calendar.getInstance();
			cal.setTime(currentDate);
			cal.add(Calendar.DAY_OF_YEAR, 1); // <--
			currentDate = cal.getTime();

			double hour = workSheetVO.getHours()[index];
			col2.setText(String.valueOf(hour));

			col3.setText(workSheetVO.getSystem()[index]);
			col4.setText(workSheetVO.getDeliverables()[index]);
		}

	}

	/**
	 * 編輯「工作日誌」內容
	 * 
	 * @param workSheetVO
	 *            修改後物件
	 * @param source
	 *            原有檔名
	 */
	public void editExcelFile(WorkSheetVO workSheetVO, String source) {
		// init
		Workbook wb = new HSSFWorkbook();

		String consultantName = workSheetVO.getConsultantName();
		Date startDate = workSheetVO.getStartDate();
		String fileName = workSheetVO.getFileName();
		StringBuilder period = new StringBuilder("Period:");

		double[] hours = workSheetVO.getHours();
		String[] systemArray = workSheetVO.getSystem();
		String[] delivarables = workSheetVO.getDeliverables();

		try {
			InputStream inputStream = new FileInputStream(
					"/sdcard/Android/data/com.billy.worksheetsenderapp/files/"
							+ source);
			wb = new HSSFWorkbook(inputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Sheet sheet = wb.getSheetAt(0);
		Row row = sheet.getRow(8);
		row.getCell(0).setCellValue(consultantName);
		row.getCell(1).setCellValue(startDate);

		SimpleDateFormat sdformat = new SimpleDateFormat("yyyy/MM/dd");
		String periodDate = sdformat.format(startDate);
		period.append(periodDate).append("~");
		
		for (int index = 0; index < 5; index++) {
			row = sheet.getRow(index + 8);

			// add 0 - 4 days
			if (index != 0) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(startDate);
				cal.add(Calendar.DAY_OF_YEAR, 1); // <--
				startDate = cal.getTime();
				row.getCell(1).setCellValue(startDate);
			}

			row.getCell(2).setCellValue(hours[index]);
			row.getCell(3).setCellValue(systemArray[index]);
			row.getCell(4).setCellValue(delivarables[index]);
		}
		
		periodDate = sdformat.format(startDate);
		period.append(periodDate);
		row = sheet.getRow(3);
		row.getCell(0).setCellValue(period.toString());
		
		// 寫入新的Excel檔
		FileOutputStream fileOut;

		try {
			fileOut = new FileOutputStream(
					"/sdcard/Android/data/com.billy.worksheetsenderapp/files/"
							+ fileName);
			wb.write(fileOut);
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			wb.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 取得 「工作日誌」內容
	 * 
	 * @param fileName
	 *            檔名
	 * @return 「工作日誌」封裝物件
	 */
	public WorkSheetVO readExcelFile(String fileName) {
		// init
		Workbook wb = new HSSFWorkbook();

		WorkSheetVO workSheetVO = new WorkSheetVO();
		workSheetVO.setFileName(fileName);

		try {
			// get workbook from file
			InputStream inputStream = new FileInputStream(
					"/sdcard/Android/data/com.billy.worksheetsenderapp/files/"
							+ fileName);
			wb = new HSSFWorkbook(inputStream);

			// 指定工作表
			Sheet sheet = wb.getSheetAt(0);

			// 指定表格列
			Row row = sheet.getRow(8);

			// 指定細格

			// consultantName
			String consultantName = row.getCell(0).getStringCellValue();

			// startDate
			Date startDate = row.getCell(1).getDateCellValue(); // startDate

			// hours
			double[] hoursArray = new double[5];

			for (int rowIndex = 8; rowIndex < 13; rowIndex++) {
				row = sheet.getRow(rowIndex);
				hoursArray[rowIndex - 8] = row.getCell(2).getNumericCellValue();
			}

			// system
			String[] systemArray = new String[5];

			for (int rowIndex = 8; rowIndex < 13; rowIndex++) {
				row = sheet.getRow(rowIndex);
				systemArray[rowIndex - 8] = row.getCell(3).getStringCellValue();
			}

			// deliverables
			String[] deliverablesArray = new String[5];

			for (int rowIndex = 8; rowIndex < 13; rowIndex++) {
				row = sheet.getRow(rowIndex);
				deliverablesArray[rowIndex - 8] = row.getCell(4)
						.getStringCellValue();
			}

			// set VO
			workSheetVO.setConsultantName(consultantName);
			workSheetVO.setStartDate(startDate);
			workSheetVO.setHours(hoursArray);
			workSheetVO.setSystem(systemArray);
			workSheetVO.setDeliverables(deliverablesArray);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			wb.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return workSheetVO;
	}

	private void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

}