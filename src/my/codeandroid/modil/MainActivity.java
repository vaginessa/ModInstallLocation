package my.codeandroid.modil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String TAG = "MODIL";

	private final String[] selection = { "0 (Auto)", "1 (Phone)",
			"2 (External)" };
	private final String getInstallLocationCommand = "pm getInstallLocation";
	private final String setInstallLocationCommand = "pm setInstallLocation";
	private Spinner installLocations;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		TextView curr = (TextView) findViewById(R.id.lbl_curr);
		installLocations = (Spinner) findViewById(R.id.spn_install_locations);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				getApplicationContext(), android.R.layout.simple_spinner_item,
				selection);
		adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		installLocations.setAdapter(adapter);

		try {
			String result = runShellCommand(getInstallLocationCommand, true);
			Log.i(TAG, "Result: " + result);
			curr.setText(curr.getText() + " "+ result);

			int location = Integer.parseInt(result.substring(0, 1));
			switch (location) {
			case 0:
				installLocations.setSelection(0);
				break;
			case 1:
				installLocations.setSelection(1);
				break;
			case 2:
				installLocations.setSelection(2);
				break;
			default:
				throw new UnsupportedOperationException();
			}

		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			Toast.makeText(this.getApplicationContext(),
					"Oops! " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	private String runShellCommand(String cmd, boolean wait) throws IOException,
			InterruptedException {
		Process process = Runtime.getRuntime().exec("su");
		DataOutputStream os = new DataOutputStream(process.getOutputStream());
		DataInputStream osRes = new DataInputStream(process.getInputStream());
		os.writeBytes(cmd + "\n");
		os.flush();
		String result = null;
		if (wait) {
			result = osRes.readLine();
		}
		os.writeBytes("exit\n");
		os.flush();
		os.close();
		osRes.close();
		return result;
	}

	public void saveBtnOnClick(View v) {
		try {
			String cmd = setInstallLocationCommand + " "
			+ installLocations.getSelectedItemPosition();
			Log.i(TAG, cmd);
			runShellCommand(cmd, false);
			Toast.makeText(this.getApplicationContext(),
					"Set installLocation successful", Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			Toast.makeText(this.getApplicationContext(),
					"Set installLocation failed!", Toast.LENGTH_LONG).show();
		}
		this.finish();
	}
}